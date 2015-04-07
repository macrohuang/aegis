package com.macrohuang.aegis.bo;

import com.macrohuang.aegis.api.BlockedConstants;
import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.api.BlockedConstants.BlockedType;
import com.macrohuang.aegis.judgement.DefaultJudgment;
import com.macrohuang.aegis.judgement.IdJugdement;
import com.macrohuang.aegis.judgement.Judgment;
import com.macrohuang.aegis.key.ArgIdxBlockedKey;
import com.macrohuang.aegis.key.BindingBlockedKey;
import com.macrohuang.aegis.key.BlockedKey;
import com.macrohuang.aegis.key.ComboBlockedKey;
import com.macrohuang.aegis.policy.ExceptionPolicy;
import com.macrohuang.aegis.policy.NullPolicy;
import com.macrohuang.aegis.policy.Policy;
import com.macrohuang.aegis.quota.BaseQuota;
import com.macrohuang.aegis.quota.Quota;
import com.macrohuang.aegis.repository.CounterRepository;
import com.macrohuang.aegis.repository.RamCounterRepository;
import com.macrohuang.aegis.repository.RedisCounterRepository;
import com.macrohuang.aegis.strategy.FrameLimitStrategy;
import com.macrohuang.aegis.strategy.FrequencyLimitStrategy;
import com.macrohuang.aegis.strategy.LimitStrategy;
import com.macrohuang.aegis.strategy.QuotaLimitStrategy;
import com.macrohuang.aegis.strategy.TimeLimitStrategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class BlockedRule implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7754420824145954200L;

    private LimitStrategy limitStrategy;

    private Policy policy;

    private BlockedKey blockedKey;

    private Judgment judgement;

    private int limit;

    private int block;

    private BlockedTimeType timeType;

    private boolean strict;

    private Lock lock = new ReentrantLock();

    private static final Logger logger = LoggerFactory.getLogger(BlockedRule.class);

    private int duration;

    public static final List<String> getAllSupportedArg() {
        return Arrays.asList("limit", "block", "key", "ids", "strategy", "policy", "type", "strict", "cluster",
                "repository", "quota");
    }

    public static class BlockedRuleBuilder {
        private String limit;
        private String key;
        private String ids;
        private String strategy;
        private String policy;
        private String type;
        private String block;
        private boolean strict = false;
        private boolean cluster = true;
        private ApplicationContext context;
        private String repository;
        private String quota;

        public BlockedRuleBuilder() {

        }

        public BlockedRuleBuilder(ApplicationContext context) {
            this.context = context;
        }

        public BlockedRuleBuilder limit(String limit) {
            this.limit = limit;
            return this;
        }

        public BlockedRuleBuilder key(String key) {
            this.key = key;
            return this;
        }

        public BlockedRuleBuilder ids(String ids) {
            this.ids = ids;
            return this;
        }

        public BlockedRuleBuilder strategy(String strategy) {
            this.strategy = strategy;
            return this;
        }

        public BlockedRuleBuilder policy(String policy) {
            this.policy = policy;
            return this;
        }

        public BlockedRuleBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BlockedRuleBuilder block(String block) {
            this.block = block;
            return this;
        }

        public BlockedRuleBuilder strict(String strict) {
            this.strict = Boolean.valueOf(strict);
            return this;
        }

        public BlockedRuleBuilder cluster(String cluster) {
            this.cluster = Boolean.valueOf(cluster);
            return this;
        }

        public BlockedRuleBuilder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public BlockedRuleBuilder quota(String quota) {
            this.quota = quota;
            return this;
        }

        @SuppressWarnings("unchecked")
        private <T> T getBean(String nameWithPrefix) {
            String beanName = nameWithPrefix.substring(BlockedConstants.BEAN_PREFIX.length());
            if (context.containsBean(beanName)) {
                return (T) context.getBean(beanName);
            } else {
                throw new IllegalArgumentException("Expect bean:" + beanName + " does not exist!!!");
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T reflect(String cls) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            return (T) this.getClass().getClassLoader().loadClass(cls).newInstance();
        }

        private void buildBase(BlockedRule blockedRule) {
            Assert.hasText(limit, "The limit must be set!");
            blockedRule.timeType = BlockedTimeType.fromTypeChar(limit.charAt(limit.length() - 1));
            if (limit.indexOf('.') > -1) {
                blockedRule.limit = Integer.parseInt(limit.substring(0, limit.indexOf('.')));
                blockedRule.duration = Integer.parseInt(limit.substring(limit.indexOf('.') + 1, limit.length() - 1));
            } else {
                blockedRule.limit = Integer.parseInt(limit.substring(0, limit.length() - 1));
                blockedRule.duration = 1;
            }
            blockedRule.strict = strict;
            if (StringUtils.isNotBlank(block)) {
                blockedRule.block = (int) (BlockedTimeType.fromTypeChar(block.charAt(block.length() - 1)).getTimeInMs() / 1000 * Integer
                        .parseInt(block.substring(0, block.length() - 1)));
            }
        }

        private void buildKey(BlockedRule blockedRule) {
            try {
                // 如果未指定key，则默认用访问参数列表中第一个参数作为key
                if (StringUtils.isBlank(key)) {
//                    ComboBlockedKey comboBlockedKey = new ComboBlockedKey();
//                    comboBlockedKey.add(0);
//                    blockedRule.blockedKey = comboBlockedKey;
                    blockedRule.blockedKey = new ArgIdxBlockedKey(0);
                } else {
                    // 指定key为绑定类型
                    if (BlockedConstants.KEY_BINKING.equalsIgnoreCase(key)) {
                        blockedRule.blockedKey = new BindingBlockedKey();
                    } else if (key.indexOf("[") > -1 && key.indexOf("]") > -1) {// 如果指定的key中含有[]，表示需要指定的是第N个参数或者第N个参数的某个属性
                        String[] keys = StringUtils.split(key, BlockedConstants.MULI_VALUE_SPLIT);
                        ComboBlockedKey comboBlockedKey = new ComboBlockedKey();
                        for (String tmp : keys) {
                            int idx = Integer.parseInt(tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("]")));
                            if (tmp.indexOf(".") > 0) {
                                comboBlockedKey.add(idx, tmp.substring(tmp.indexOf(".") + 1));
                            } else {
                                comboBlockedKey.add(idx);
                            }
                        }
                        blockedRule.blockedKey = comboBlockedKey;
                    } else if (key.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean
                        blockedRule.blockedKey = getBean(key);
                    } else {// 否则则用用户自己实现的key
                        blockedRule.blockedKey = reflect(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                blockedRule.blockedKey = new ArgIdxBlockedKey(0);
            }
        }

        private void buildQuota(BlockedRule blockedRule) {
            if (StringUtils.isNotBlank(quota)) {
                Quota q = null;
                if (quota.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean定义
                    q = getBean(quota);
                } else if (quota.indexOf(BlockedConstants.VALUE_ASSIGN_CHAR) > -1) {// 使用key=value定义
                    q = new BaseQuota(blockedRule.limit, blockedRule.blockedKey);
                    if (quota.indexOf(BlockedConstants.MULI_VALUE_SPLIT) > 0) {
                        String[] kvs = StringUtils.split(quota, BlockedConstants.MULI_VALUE_SPLIT);
                        for (String kv : kvs) {
                            String[] tmp = StringUtils.split(kv, BlockedConstants.VALUE_ASSIGN_CHAR);
                            if (tmp.length < 2) {
                                logger.warn("Bad quota:" + kv);
                                continue;
                            }
                            ((BaseQuota) q).addQuota(tmp[0].trim(), Integer.parseInt(tmp[1].trim()));
                        }
                    }
                } else {// 使用自定义class
                    try {
                        q = reflect(quota);
                    } catch (Exception e) {
                        logger.error("Error while reflect quota:" + e.getLocalizedMessage(), e);
                    }
                }
                ((QuotaLimitStrategy) blockedRule.limitStrategy).setQuota(q);
            }
        }

        private void buildStrategy(BlockedRule blockedRule) {
            if (StringUtils.isNotBlank(strategy)) {
                try {
                    // 指定了按频率的策略
                    if (BlockedConstants.STRATEGY_FREQUENCE.equalsIgnoreCase(strategy)) {
                        blockedRule.limitStrategy = new FrequencyLimitStrategy(blockedRule.timeType, blockedRule.limit,
                                blockedRule.block);
                    } else if (BlockedConstants.STRATEGY_FRAME.equalsIgnoreCase(strategy)) {// 指定按流动时间累积计数的策略
                        blockedRule.limitStrategy = new FrameLimitStrategy(blockedRule.timeType, blockedRule.limit,
                                blockedRule.block);
                    } else if (BlockedConstants.STRATEGY_QUOTA.equalsIgnoreCase(strategy)) {// 按quota的策略
                        blockedRule.limitStrategy = new QuotaLimitStrategy(blockedRule.timeType, blockedRule.limit,
                                blockedRule.block);
                        buildQuota(blockedRule);
                    } else if (strategy.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean
                        blockedRule.limitStrategy = getBean(strategy);
                    } else {// 用户自己定义的策略
                        blockedRule.limitStrategy = reflect(strategy);
                    }
                } catch (Exception e) {
                    logger.warn("Try to set custom strategy fail, use default TimeLimitStrategy instead.", e);
                    blockedRule.limitStrategy = new TimeLimitStrategy(blockedRule.timeType, blockedRule.limit,
                            blockedRule.block);
                }
            } else {// 如果未指定策略，则默认按单位时间来策略
                blockedRule.limitStrategy = new TimeLimitStrategy(blockedRule.timeType, blockedRule.limit,
                        blockedRule.block);
            }
            blockedRule.limitStrategy.setLimit(blockedRule.limit);
            blockedRule.limitStrategy.setTimeType(blockedRule.timeType);
            blockedRule.limitStrategy.setBlock(blockedRule.block);
            blockedRule.limitStrategy.setDuration(blockedRule.duration);
        }

        private void buildPolicy(BlockedRule blockedRule) {
            // 未指定超出访问限制的做法，默认用返回空的做法
            if (StringUtils.isBlank(policy)) {
                blockedRule.policy = new NullPolicy();
            } else {
                try {
                    // 指定了超出访问限制抛异常的做法
                    if (BlockedConstants.POLICY_EXCEPTION.equalsIgnoreCase(policy)) {
                        blockedRule.policy = new ExceptionPolicy(blockedRule.timeType, blockedRule.limit);
                    } else if (policy.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean
                        blockedRule.policy = getBean(policy);
                    } else {// 用户自定义的超出访问限制处理办法
                        blockedRule.policy = reflect(policy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    blockedRule.policy = new NullPolicy();
                }
            }
        }

        private void buildJudgment(BlockedRule blockedRule) {
            // 未指定ID，则对所有用户所有访问生效
            if (StringUtils.isBlank(ids)) {
                blockedRule.judgement = new DefaultJudgment();
            } else {
                try {
                    // 指定了自定义的ID过滤算法
                    if (ids.indexOf(".") > 0) {
                        blockedRule.judgement = reflect(ids);
                    } else if (ids.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean
                        blockedRule.judgement = getBean(ids);
                    } else {// 指定了ID，且未指定类型，则默认用黑名单的方式
                        if (StringUtils.isBlank(type) || !BlockedType.White.toString().equalsIgnoreCase(type)) {
                            blockedRule.judgement = new IdJugdement(BlockedType.Black, new HashSet<String>(
                                    Arrays.asList(StringUtils.split(ids, BlockedConstants.MULI_VALUE_SPLIT))));
                        } else {
                            blockedRule.judgement = new IdJugdement(BlockedType.White, new HashSet<String>(
                                    Arrays.asList(StringUtils.split(ids, BlockedConstants.MULI_VALUE_SPLIT))));
                        }
                    }
                } catch (Exception e) {
                    blockedRule.judgement = new DefaultJudgment();
                }
            }
        }

        private void defaultCounterRepository(BlockedRule blockedRule) {
            if (cluster) {
                blockedRule.limitStrategy.setCounterRepository(context.getBean("redisCounterRepository",
                        RedisCounterRepository.class));
            } else {
                blockedRule.limitStrategy.setCounterRepository(context.getBean("ramCounterRepository",
                        RamCounterRepository.class));
            }
        }

        private void buildCounterRepository(BlockedRule blockedRule) {
            if (StringUtils.isBlank(repository)) {
                if (blockedRule.limitStrategy == null) {
                    buildStrategy(blockedRule);
                }
                defaultCounterRepository(blockedRule);
            } else {
                try {
                    // 指定了自定义的ID过滤算法
                    if (repository.indexOf(".") > 0) {
                        blockedRule.limitStrategy.setCounterRepository((CounterRepository) reflect(repository));
                    } else if (repository.startsWith(BlockedConstants.BEAN_PREFIX)) {// 使用bean
                        blockedRule.limitStrategy.setCounterRepository((CounterRepository) getBean(repository));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    defaultCounterRepository(blockedRule);
                }
            }
        }

        public BlockedRule build() {
            BlockedRule blockedRule = new BlockedRule();
            buildBase(blockedRule);
            buildKey(blockedRule);
            buildStrategy(blockedRule);
            buildPolicy(blockedRule);
            buildJudgment(blockedRule);
            buildCounterRepository(blockedRule);
            return blockedRule;
        }
    }

    /**
     * @return the lock
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * @return the limitStrategy
     */
    public LimitStrategy getLimitStrategy() {
        return limitStrategy;
    }

    /**
     * @return the policy
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * @return the blockedKey
     */
    public BlockedKey getBlockedKey() {
        return blockedKey;
    }

    /**
     * @return the judgement
     */
    public Judgment getJudgement() {
        return judgement;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @return the block
     */
    public int getBlock() {
        return block;
    }

    /**
     * @return the timeType
     */
    public BlockedTimeType getTimeType() {
        return timeType;
    }

    /**
     * @return the strict
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}

package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.bo.BlockedPoint;
import com.baidu.fengchao.skynet.repository.CounterRepository;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 
 * @author Macro Huang
 * 
 */
public abstract class AbstractLimitStrategy implements LimitStrategy {
    private BlockedTimeType timeType;
    private long limit;
    private long block;
    private int duration = 1;
    protected static final String BLOCKED_KEY_CACHE_PREFIX = "blocked_key_prefix_";
    protected static final String BLOCKED_KEY_CACHE_LIST_PREFIX = "blocked_key_list_prefix_";
    protected static final String BLOCKED_KEY_CACHE_COUNT_PREFIX = "blocked_key_count_prefix_";
    protected static final String BLOCKED_CACHE_PREFIX = "blocked_prefix_";
    private CounterRepository repository;

    public AbstractLimitStrategy() {

    }

    public AbstractLimitStrategy(BlockedTimeType timeType, long limit) {
        super();
        this.timeType = timeType;
        this.limit = limit;
    }

    public AbstractLimitStrategy(BlockedTimeType timeType, long limit, long block) {
        this(timeType, limit);
        this.block = block;
    }

    @Override
    public boolean isExceedLimit(BlockedPoint point, Object blockedKey, Object... params) {
        // 只有block大于0的才会可能阻止访问，避免不必要的redis调用
        if (block > 0 && isBlocked(point, blockedKey)) {
            return true;
        }
        boolean exceed = exceedLimit(point, blockedKey, params);
        if (exceed && block > 0) {
            setBlockedFlag(point, blockedKey, block);
        }
        return exceed;
    }

    protected abstract boolean exceedLimit(BlockedPoint point, Object blockedKey, Object... params);

    protected String getId(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
    }

    protected boolean isBlocked(BlockedPoint point, Object key, Object... params) {
        try {
            return getCounterRepository().getBlockedFlag(getBlockedCacheKey(point, key));
        } catch (Exception e) {
            return false;
        }
    }

    protected void setBlockedFlag(BlockedPoint point, Object key, long timeoutInSeconds, Object... params) {
        try {
            getCounterRepository().setBlockedFlag(getBlockedCacheKey(point, key));
            getCounterRepository().expire(getBlockedCacheKey(point, key), (int) timeoutInSeconds);
        } catch (Exception e) {
            // Set blocked flag fail.
            e.printStackTrace();
        }
    }

    public BlockedTimeType getTimeType() {
        return timeType;
    }

    public long getLimit(Object... params) {
        return limit;
    }

    public long getBlock() {
        return block;
    }

    public int getDuration() {
        return duration;
    }

    public void setTimeType(BlockedTimeType timeType) {
        this.timeType = timeType;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected String getCacheKey(BlockedPoint point, Object blockedKey, Object... params) {
        return BLOCKED_KEY_CACHE_PREFIX + point.getPointId() + blockedKey;
    }

    protected String getCacheListKey(BlockedPoint point, Object blockedKey, Object... params) {
        return BLOCKED_KEY_CACHE_LIST_PREFIX + point.getPointId() + blockedKey;
    }

    protected String getCacheCountKey(BlockedPoint point, Object blockedKey, Object... params) {
        return BLOCKED_KEY_CACHE_COUNT_PREFIX + point.getPointId() + blockedKey;
    }

    private String getBlockedCacheKey(BlockedPoint point, Object key, Object... params) {
        return BLOCKED_CACHE_PREFIX + point.getPointId() + key;
    }

    public CounterRepository getCounterRepository() {
        return repository;
    }

    public void setCounterRepository(CounterRepository repository) {
        this.repository = repository;
    }

}

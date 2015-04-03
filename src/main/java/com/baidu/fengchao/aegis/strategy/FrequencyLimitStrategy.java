package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.bo.BlockedPoint;

import org.apache.log4j.Logger;

/**
 * Frequency based strategy. It will convert limit into frequency, for example,
 * 60 time per minute will be convert into every two invoke can't be less than 1
 * second, and so on. The minimum unit of time is millisecond.
 * 
 * @author Macro Huang
 * 
 */
public class FrequencyLimitStrategy extends AbstractLimitStrategy {
    private static final Logger LOGGER = Logger.getLogger(FrequencyLimitStrategy.class);
    public FrequencyLimitStrategy(BlockedTimeType timeType, long limit) {
        super(timeType, limit);
    }

    public FrequencyLimitStrategy(BlockedTimeType timeType, long limit, int block) {
        super(timeType, limit, block);
    }

    protected long getFrequency(Object... params) {
        return getTimeType().getFrequency(getLimit(params)) * getDuration();
    }

    @Override
    public boolean exceedLimit(BlockedPoint point, Object blockedKey, Object... params) {
        long lastAccessTime = -1;
        long currentAccessTime = System.currentTimeMillis();
        boolean exceed = getLimit(params) <= 0;
        if (!exceed) {
            int cacheExperis = (int) (getTimeType().getTimeInMs() / 1000);
            try {
                lastAccessTime = getCounterRepository().getLastAccessTime(getCacheKey(point, blockedKey, params));
            } catch (Exception e) {
                LOGGER.error("Error while get lastAccessTime from redis:" + e.getLocalizedMessage(), e);
            }
            if (lastAccessTime > 0 && currentAccessTime - lastAccessTime < getFrequency(params)) {
                exceed = true;
            }
            try {
                getCounterRepository().updateLastAccessTime(getCacheKey(point, blockedKey, params), currentAccessTime);
                // 设置缓存失效时间
                getCounterRepository().expire(getCacheKey(point, blockedKey, params), cacheExperis);
            } catch (Exception e) {
                // update cache fail
                LOGGER.error("Error while set lastAccessTime to redis:" + e.getLocalizedMessage(), e);
            }
        }
        return exceed;
    }

    @Override
    public String toString() {
        return "FrequencyLimitStrategy [frequency=" + getFrequency() + "]";
    }
}

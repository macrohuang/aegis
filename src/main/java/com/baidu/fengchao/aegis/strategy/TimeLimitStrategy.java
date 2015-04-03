package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.bo.BlockedPoint;

import org.apache.log4j.Logger;

/**
 * Total count in a nature time unit base limit strategy. For example, from
 * 10:00:20 - 10:01:19 will be treated as two minutes, separate as 10:00:00 and
 * 10:01:00
 * 
 * 
 * @author Macro Huang
 * 
 */
public class TimeLimitStrategy extends AbstractLimitStrategy {
    private static final Logger LOGGER = Logger.getLogger(TimeLimitStrategy.class);

    public TimeLimitStrategy(BlockedTimeType timeType, long limit) {
        super(timeType, limit);
    }

    public TimeLimitStrategy(BlockedTimeType timeType, long limit, int block) {
        super(timeType, limit, block);
    }

    @Override
    public boolean exceedLimit(BlockedPoint point, Object blockedKey, Object... params) {
        long count = 0, lastAccessTime = -1, currentAccessTime = System.currentTimeMillis();
        boolean exceed = getLimit(params) <= 0;
        if (!exceed) {
            try {
                count = getCounterRepository().getAccessCount(getCacheCountKey(point, blockedKey, params));
                lastAccessTime = getCounterRepository().getLastAccessTime(
getCacheKey(point, blockedKey, params));
            } catch (Exception e) {
                LOGGER.error("Error while get lastAccessTime:" + e.getLocalizedMessage(), e);
            }
            boolean expries = count == 0;
            // The same time unit.
            if ((currentAccessTime / getTimeType().getTimeInMs())
                    - (lastAccessTime / getTimeType().getTimeInMs()) < getDuration()) {
                if (count >= getLimit(params)) {
                    exceed = true;
                } else {
                    exceed = false;
                }
            } else if (lastAccessTime > 0) {// Not the same time unit, clear access
                // count.
                getCounterRepository().decreaseAccessCountBy(getCacheCountKey(point, blockedKey, params),
                        count);
                expries = true;
            }
            try {
                getCounterRepository().updateLastAccessTime(getCacheKey(point, blockedKey, params),
                        currentAccessTime);
                getCounterRepository().increaseAccessCountBy(getCacheCountKey(point, blockedKey, params),
                        getAccessCount(params));

                if (expries) {
                    // 缓存失效时间，防止redis被长期占用,只需要在第一次访问时设置
                    //因为这个是按自然时间来封禁的
                    int experisTimeInSecond = (int) (getTimeType().getTimeInMs() / 1000
                            * getDuration() - currentAccessTime / 1000
                            % (getTimeType().getTimeInMs() / 1000));

                    // 如果时间单位是天或者比天大的，需要减去格林治时间起始时间的8小时
                    if (getTimeType().getTimeInMs() > 1000 * 3600) {
                        experisTimeInSecond -= 8 * 3600;
                    }
                    getCounterRepository().expire(getCacheKey(point, blockedKey, params),
                            experisTimeInSecond);
                    getCounterRepository().expire(getCacheCountKey(point, blockedKey, params),
                            experisTimeInSecond);
                }
            } catch (Exception e) {
                // update cache fail
                LOGGER.error("Error while update last access time:" + e.getLocalizedMessage(), e);
            }
        }
        return exceed;
    }

    /**
     * 一次请求后要计多少数
     * @param params 请求的参数
     * @return 一次成功的请求将被计数的数量，默认为1.
     */
    protected int getAccessCount(Object... params) {
        return 1;
    }
    @Override
    public String toString() {
        return "TimeLimitStrategy [getTimeType()=" + getTimeType() + ", getLimit()=" + getLimit()
                + "]";
    }
}

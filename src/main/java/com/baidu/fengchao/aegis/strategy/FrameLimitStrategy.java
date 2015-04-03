package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.bo.BlockedPoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Total count in every time unit base limit strategy. For example, from
 * 10:00:20 - 10:01:19 will be treated as one minute.
 * 
 * @author Macro
 * 
 */
public class FrameLimitStrategy extends AbstractLimitStrategy {
    private static final Logger LOGGER = Logger.getLogger(FrameLimitStrategy.class);
    public FrameLimitStrategy() {

    }

    public FrameLimitStrategy(BlockedTimeType timeType, long limit) {
        super(timeType, limit);
    }

    public FrameLimitStrategy(BlockedTimeType timeType, long limit, int block) {
        super(timeType, limit, block);
    }

    @Override
    public boolean exceedLimit(BlockedPoint point, Object blockedKey, Object... params) {
        long currentAccessTime = System.currentTimeMillis();
        boolean exceed = getLimit() <= 0;
        if (!exceed) {
            List<Long> accesses = new ArrayList<Long>();
            try {
                accesses = getCounterRepository().getAccessList(getCacheListKey(point, blockedKey, params));
            } catch (Exception e) {
                LOGGER.error("Error while get access list:" + e.getLocalizedMessage(), e);
            }
            int count = 0;
            int expireInSeconds = (int) (getTimeType().getTimeInMs() / 1000) * getDuration();
            for (Long l : accesses) {
                if (currentAccessTime - l > super.getTimeType().getTimeInMs() * getDuration()) {
                    count++;
                } else {
                    break;
                }
            }
            if (accesses.size() - count + 1 > getLimit(params)) {
                exceed = true;
            }
            try {
                getCounterRepository().addAccessRecord(getCacheListKey(point, blockedKey, params), currentAccessTime);
                if (count > 0) {
                    getCounterRepository().removeAccessRecord(getCacheListKey(point, blockedKey, params), count);
                }
                getCounterRepository().expire(getCacheListKey(point, blockedKey, params), expireInSeconds);
            } catch (Exception e) {
                // Update cache fail
                LOGGER.error("Error while update access list:" + e.getLocalizedMessage(), e);
            }
        }
        return exceed;
    }

    @Override
    public String toString() {
        return "CountLimitStrategy [getTimeType()=" + getTimeType() + ", getLimit()=" + getLimit() + "]";
    }
}

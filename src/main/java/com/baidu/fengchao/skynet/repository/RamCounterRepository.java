package com.baidu.fengchao.skynet.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public class RamCounterRepository implements CounterRepository {
    private final Map<String, Object> accessMap = new ConcurrentHashMap<String, Object>();
    private final Timer timer = new Timer("Delete access record timer");
    private final static Logger LOGGER = Logger.getLogger(RamCounterRepository.class);

    public RamCounterRepository() {

    }

    private Number getNumberValue(String key) {
        if (accessMap.containsKey(key)) {
            Object val = accessMap.get(key);
            if (val instanceof Number) {
                return (Number) val;
            } else {
                LOGGER.warn("Required a number of key [" + key + "], but was[" + val.getClass().getName() + "]");
            }
        }
        return 0;
    }

    @Override
    public long getLastAccessTime(String key) {
        return getNumberValue(key).longValue();
    }

    @Override
    public void updateLastAccessTime(String key, long accessTime) {
        accessMap.put(key, accessTime);
    }

    @Override
    public long getAccessCount(String key) {
        return getNumberValue(key).longValue();
    }

    @Override
    public void increaseAccessCount(String key) {
        increaseAccessCountBy(key, 1L);
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.skynet.repository.CounterRepository#increaseAccessCountBy(java.lang.String, int)
     */
    @Override
    public void increaseAccessCountBy(String key, long count) {
        if (getNumberValue(key) instanceof AtomicLong) {
            AtomicLong accessCount = (AtomicLong) getNumberValue(key);
            accessCount.addAndGet(count);
        } else {
            accessMap.put(key, new AtomicLong(count));
        }
    }

    @Override
    public void decreaseAccessCountBy(String key, long count) {
        if (getNumberValue(key) instanceof AtomicLong) {
            AtomicLong accessCount = (AtomicLong) getNumberValue(key);
            accessCount.addAndGet(-1 * count);
        } else {
            LOGGER.warn("Required AtomicLong of key [" + key + "], but was ["
                    + getNumberValue(key).getClass().getName() + "]");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getAccessList(String key) {
        List<Long> list = new CopyOnWriteArrayList<Long>();
        if (accessMap.containsKey(key)) {
            Object val = accessMap.get(key);
            if (val instanceof List<?>) {
                list = (List<Long>) val;
            } else {
                LOGGER.warn("Required A List of key [" + key + "], but was [" + val.getClass().getName() + "]");
            }
        } else {
            accessMap.put(key, list);
        }
        return list;
    }

    @Override
    public void addAccessRecord(String key, long accessTime) {
        getAccessList(key).add(accessTime);
    }

    @Override
    public void removeAccessRecord(String key, int count) {
        List<Long> accessList = getAccessList(key);
        if (count > 0) {
            if (count > accessList.size()) {
                accessList.clear();
            } else {
                accessList = accessList.subList(count, accessList.size());
            }
            accessMap.put(key, accessList);
        }
    }

    @Override
    public void expire(final String key, int seconds) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                accessMap.remove(key);
            }
        }, seconds * 1000);
    }

    @Override
    public boolean getBlockedFlag(String key) {
        return accessMap.containsKey(key);
    }

    @Override
    public void setBlockedFlag(String key) {
        accessMap.put(key, Boolean.TRUE);

    }

    @Override
    public void expireAt(final String key, long unixSeconds) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                accessMap.remove(key);
            }
        }, new Date(unixSeconds * 1000));
    }
}

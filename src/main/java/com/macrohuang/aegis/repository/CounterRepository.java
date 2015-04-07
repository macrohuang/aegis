package com.macrohuang.aegis.repository;

import java.util.List;

public interface CounterRepository {

    public long getLastAccessTime(String key);

    public void updateLastAccessTime(String key, long accessTime);

    public long getAccessCount(String key);

    public void increaseAccessCount(String key);

    public void increaseAccessCountBy(String key, long count);

    public void decreaseAccessCountBy(String key, long count);

    public List<Long> getAccessList(String key);

    public void addAccessRecord(String key, long accessTime);

    public void removeAccessRecord(String key, int count);

    public void expire(String key, int seconds);

    public boolean getBlockedFlag(String key);

    void setBlockedFlag(String key);

    void expireAt(String key, long unixSeconds);
}

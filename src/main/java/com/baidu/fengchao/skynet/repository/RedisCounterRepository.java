package com.baidu.fengchao.skynet.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;

import com.baidu.fengchao.skynet.util.NumberBytesConvertUtil;

public class RedisCounterRepository extends ShardingPool implements CounterRepository {

    @Override
    public void addAccessRecord(String key, long accessTime) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            jedis.rpush(key.getBytes(), String.valueOf(accessTime).getBytes());
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public List<Long> getAccessList(String key) {
        Jedis jedis = getConnection(getShardID(key));
        List<Long> accesses = new ArrayList<Long>();
        try {
            List<byte[]> accessList = jedis.lrange(key.getBytes(), 0, -1);
            for (byte[] bytes : accessList) {
                accesses.add(Long.parseLong(new String(bytes)));
            }
        } finally {
            returnConnection(key, jedis);
        }
        return accesses;
    }

    @Override
    public void removeAccessRecord(String key, int count) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            for (int i = 0; i < count; i++)
                jedis.lpop(key.getBytes());
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public long getLastAccessTime(String key) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            byte[] val = jedis.get(key.getBytes());
            return (val == null || val.length == 0) ? 0 : NumberBytesConvertUtil.byteArr2Long(val);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public void updateLastAccessTime(String key, long accessTime) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            byte[] bs = jedis.getSet(key.getBytes(), NumberBytesConvertUtil.long2ByteArr(accessTime));
            long lastAccessTime = -1;
            if (bs != null && bs.length > 0) {
                lastAccessTime = NumberBytesConvertUtil.byteArr2Long(bs);
            }
            while (lastAccessTime > accessTime) {
                accessTime = lastAccessTime;
                lastAccessTime = NumberBytesConvertUtil.byteArr2Long(jedis.getSet(key.getBytes(),
                        NumberBytesConvertUtil.long2ByteArr(accessTime)));
            }
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public long getAccessCount(String key) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            String val = jedis.get(key);
            return StringUtils.isBlank(val) ? 0 : Long.parseLong(val);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public void increaseAccessCount(String key) {
        increaseAccessCountBy(key, 1L);
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.skynet.repository.CounterRepository#increaseAccessCountBy(java.lang.String, long)
     */
    @Override
    public void increaseAccessCountBy(String key, long count) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            jedis.incrBy(key.getBytes(), count);
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public void decreaseAccessCountBy(String key, long count) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            jedis.decrBy(key.getBytes(), count);
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public void expire(String key, int seconds) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            // jedis.expire(key, seconds);
            jedis.expire(key.getBytes(), seconds);
        } finally {
            returnConnection(key, jedis);
        }

    }

    @Override
    public void expireAt(String key, long unixSeconds) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            // jedis.expire(key, seconds);
            jedis.expireAt(key.getBytes(), unixSeconds);
        } finally {
            returnConnection(key, jedis);
        }

    }

    @Override
    public boolean getBlockedFlag(String key) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            return jedis.exists(key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            returnConnection(key, jedis);
        }
    }

    @Override
    public void setBlockedFlag(String key) {
        Jedis jedis = getConnection(getShardID(key));
        try {
            jedis.set(key.getBytes(), new byte[] { 0 });
        } finally {
            returnConnection(key, jedis);
        }
    }
}

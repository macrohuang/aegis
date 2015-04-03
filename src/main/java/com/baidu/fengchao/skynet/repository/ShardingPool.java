/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.baidu.fengchao.skynet.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author http://jiaqing.me
 * @author Tianlai Huang(huangtianlai@baidu.com)
 * @version $Id: ShardingPool.java, v 0.1 2012-11-29 3:18:44 zhengjiaqing Exp
 *          $
 */
public class ShardingPool {
    private static final Logger LOG = LoggerFactory.getLogger(ShardingPool.class);

    private static final int DEFAULT_PORT = 6379;
    private static final int DEFAULT_SHARDS = 1;
    private static Map<String, JedisPool> shardingPool;
    private static int shards = DEFAULT_SHARDS;
    private String address;
    private int timeout;
    private String passwd;
    private int maxActive;
    private int maxIdle;
    private int maxWait;
    private boolean testOnBorrow;

    public ShardingPool() {

    }

    protected int getShards() {
        return ShardingPool.shards;
    }

    protected String getShardID(String serviceID) {
        if (serviceID == null || shards == 1) {
            return "0";
        }

        int shardID = 0;
        char[] cs = serviceID.toCharArray();
        for (char c : cs) {
            shardID += c;
        }
        return String.valueOf(shardID % shards);
    }

    public Jedis getConnection(String shardID) {
        JedisPool pool = shardingPool.get(shardID);
        return pool.getResource();
    }

    public void returnConnection(String serviceID, Jedis connection) {
        try {
            if (serviceID == null || connection == null) {
                return;
            }

            shardingPool.get(getShardID(serviceID)).returnResource(connection);
        } catch (Exception e) {
            LOG.error(
                    "[AEGIS]Jedis return connection fail! key[" + getShardID(serviceID) + "] shard["
                            + connection.toString() + "]", e);
        }
    }

    public void init() {
        String[] redisAddress = StringUtils.split(getAddress(), ";");

        if (redisAddress == null || redisAddress.length == 0) {
            LOG.error("[AEGIS]no redis address config !!");
            throw new RuntimeException("[AEGIS]no redis address config !!");
        }

        shards = redisAddress.length;
        shardingPool = new HashMap<String, JedisPool>(getShards());
        for (int i = 0; i < redisAddress.length; i++) {
            try {
                String[] fullAddress = StringUtils.split(redisAddress[i], ":");
                String ip = fullAddress[0];
                int port = ((fullAddress.length > 1 && fullAddress[1] != null) ? Integer
                        .valueOf(fullAddress[1]) : DEFAULT_PORT);

                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxActive(getMaxActive());
                poolConfig.setMaxIdle(getMaxIdle());
                poolConfig.setMaxWait(getMaxWait());
                poolConfig.setTestOnBorrow(isTestOnBorrow());

                shardingPool.put(i + "", new JedisPool(poolConfig, ip, port, getTimeout(),
                        getPasswd()));

                LOG.info(
                        "[AEGIS]init jedis pool address[{}] maxActive[{}] maxIdel[{}] maxWait[{}] testOnBorrow[{}] timeOut[{}]",
                        new Object[] { redisAddress[i], getMaxActive(), getMaxIdle(), getMaxWait(),
                                isTestOnBorrow(), getTimeout() });
            } catch (Exception e) {
                LOG.error("[AEGIS]Connecting Redis[" + redisAddress[i] + "] fail", e);
                throw new RuntimeException(e);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Set<Entry<String, JedisPool>> poolSet = shardingPool.entrySet();
                for (Entry<String, JedisPool> pool : poolSet) {
                    pool.getValue().destroy();
                }
            }
        });
    }
    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the passwd
     */
    public String getPasswd() {
        return StringUtils.isBlank(passwd) ? null : passwd;
    }

    /**
     * @param passwd the passwd to set
     */
    public void setPasswd(String passwd) {
        this.passwd = StringUtils.isBlank(passwd) ? null : passwd;
    }

    /**
     * @return the maxActive
     */
    public int getMaxActive() {
        return maxActive;
    }

    /**
     * @param maxActive the maxActive to set
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * @return the maxIdel
     */
    public int getMaxIdle() {
        return maxIdle;
    }

    /**
     * @param maxIdel the maxIdel to set
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * @return the maxWait
     */
    public int getMaxWait() {
        return maxWait;
    }

    /**
     * @param maxWait the maxWait to set
     */
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * @return the testOnBorrow
     */
    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * @param testOnBorrow the testOnBorrow to set
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
}

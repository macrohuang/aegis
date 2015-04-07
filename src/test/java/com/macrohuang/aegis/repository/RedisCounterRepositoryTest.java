/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import redis.clients.jedis.Jedis;

/**
 * @title RedisCounterRepositoryTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-7-7
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisCounterRepositoryTest {
    RedisCounterRepository counterRepository;
    String key = "aegis-unit-test";
    Jedis jedis;

    @Before
    public void setup() {
        counterRepository = new RedisCounterRepository();
        counterRepository.setAddress("10.36.55.45:9000;10.36.55.45:9000;10.36.55.45:9000");
        counterRepository.setMaxActive(20);
        counterRepository.setMaxIdle(10);
        counterRepository.setMaxWait(1000);
        counterRepository.setTestOnBorrow(false);
        counterRepository.setTimeout(6000);
        counterRepository.init();

        jedis = new Jedis("10.36.55.45", 9000);
    }

    @Test
    public void testGetBlockedFlag() {
        counterRepository.setBlockedFlag(key);
        assertTrue(counterRepository.getBlockedFlag(key));
    }

    @Test
    public void testExpireAt() throws InterruptedException {
        counterRepository.setBlockedFlag(key);
        assertTrue(counterRepository.getBlockedFlag(key));
        counterRepository.expireAt(key, System.currentTimeMillis() / 1000 + 1);
        Thread.sleep(2000);
        assertFalse(counterRepository.getBlockedFlag(key));
    }

    @Test
    public void testIncreaseAccessCount() {
        counterRepository.increaseAccessCount(key);
        assertEquals(1, counterRepository.getAccessCount(key));
    }

    @Test
    public void clear() {
        jedis.del(key);
    }
}

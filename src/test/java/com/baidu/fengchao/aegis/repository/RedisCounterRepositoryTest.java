/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import redis.clients.jedis.Jedis;

import com.baidu.fengchao.skynet.repository.RedisCounterRepository;

/**
 * @title RedisCounterRepositoryTest
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-7-7
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisCounterRepositoryTest {
    RedisCounterRepository counterRepository;
    String key = "skynet-unit-test";
    Jedis jedis;

    @Before
    public void setup() {
        counterRepository = new RedisCounterRepository();
        counterRepository
                .setAddress("dbl-fc-rd06.vm.baidu.com:6379;dbl-fc-rd06.vm.baidu.com;dbl-fc-rd06.vm.baidu.com:6379");
        counterRepository.setMaxActive(20);
        counterRepository.setMaxIdle(10);
        counterRepository.setMaxWait(1000);
        counterRepository.setPasswd("altair-taskcenter");
        counterRepository.setTestOnBorrow(false);
        counterRepository.setTimeout(6000);
        counterRepository.init();

        jedis = new Jedis("dbl-fc-rd06.vm.baidu.com", 6379);
        jedis.auth("altair-taskcenter");
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

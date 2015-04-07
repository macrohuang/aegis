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

import com.macrohuang.aegis.repository.CounterRepository;
import com.macrohuang.aegis.repository.RamCounterRepository;

/**
 * @title RamCounterRepositoryTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-7-7
 * @version 1.0
 */
public class RamCounterRepositoryTest {
    CounterRepository counterRepository;
    String key = "unit-test-key";

    @Before
    public void setup() {
        counterRepository = new RamCounterRepository();
    }
    @Test
    public void testIncreaseAccessCount() {
        counterRepository.increaseAccessCount(key);
        assertEquals(1, counterRepository.getAccessCount(key));
    }

    @Test
    public void testRemoveAccessRecord() {
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.removeAccessRecord(key, 1);
        assertEquals(2, counterRepository.getAccessList(key).size());
    }

    @Test
    public void testGetBlockedFlag() {
        assertFalse(counterRepository.getBlockedFlag(key));
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
    public void testGetAccessList() {
        counterRepository.increaseAccessCount(key);
        assertTrue(counterRepository.getAccessList(key).isEmpty());
    }

    @Test
    public void testDecreaseAccessCountBy() {
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.addAccessRecord(key, System.currentTimeMillis());
        counterRepository.decreaseAccessCountBy(key, 1);
        assertEquals(0, counterRepository.getAccessCount(key));
    }
}

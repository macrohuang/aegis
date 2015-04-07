/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.quota.Quota;
import com.macrohuang.aegis.repository.RamCounterRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @title QuotaStrategyTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class QuotaStrategyTest {
    @Mock
    Quota quota;
    @Mock
    BlockedPoint point;
    int limit = 5;
    QuotaLimitStrategy strategy = new QuotaLimitStrategy(BlockedTimeType.Minute, limit);
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        strategy.setCounterRepository(new RamCounterRepository());
        strategy.setQuota(quota);

        when(quota.getQuota(1L)).thenReturn(limit * 2);
        when(quota.getQuota(2L)).thenReturn(limit * 4);
        when(quota.getQuota(3L)).thenReturn(limit * 6);
        when(quota.getQuota(4L)).thenReturn(limit);

        when(point.getPointId()).thenReturn("quota-unit-test");
    }

    @Test
    public void testExceedLimit() {
        for (int i = 0; i < limit * 10; i++) {
            if (i < limit * 6) {
                assertFalse(strategy.exceedLimit(point, 3L, 3L));
            } else {
                assertTrue(strategy.exceedLimit(point, 3L, 3L));
            }
            if (i < limit * 4) {
                assertFalse(strategy.exceedLimit(point, 2L, 2L));
            } else {
                assertTrue(strategy.exceedLimit(point, 2L, 2L));
            }
            if (i < limit * 2) {
                assertFalse(strategy.exceedLimit(point, 1L, 1L));
            } else {
                assertTrue(strategy.exceedLimit(point, 1L, 1L));
            }
            if (i < limit) {
                assertFalse(strategy.exceedLimit(point, 4L, 4L));
            } else {
                assertTrue(strategy.exceedLimit(point, 4L, 4L));
            }
        }
    }

    @Test
    public void testGetLimit() {
        assertEquals(limit * 2, strategy.getLimit(1L));
        assertEquals(limit * 4, strategy.getLimit(2L));
        assertEquals(limit * 6, strategy.getLimit(3L));
        assertEquals(limit, strategy.getLimit(4L));
        strategy.setQuota(null);
        assertEquals(limit, strategy.getLimit(1L));
        assertEquals(limit, strategy.getLimit(2L));
        assertEquals(limit, strategy.getLimit(3L));
    }

    @Test
    public void testCustomQuotaStrategy() {
        strategy = new CustomQuotaStrategy(BlockedTimeType.Day, limit);
        strategy.setCounterRepository(new RamCounterRepository());
        strategy.setQuota(quota);
        when(point.getPointId()).thenReturn("quota-unit-test2");

        for (int i = 0; i < limit * 10; i++) {
            if (i < limit * 3) {
                assertFalse(strategy.exceedLimit(point, 3L, 3L));
            } else {
                assertTrue(strategy.exceedLimit(point, 3L, 3L));
            }
            if (i < limit * 2) {
                assertFalse(strategy.exceedLimit(point, 2L, 2L));
            } else {
                assertTrue(strategy.exceedLimit(point, 2L, 2L));
            }
            if (i < limit) {
                assertFalse(strategy.exceedLimit(point, 1L, 1L));
            } else {
                assertTrue(strategy.exceedLimit(point, 1L, 1L));
            }
            if (i < (limit - 1) / 2 + 1) {
                assertFalse(strategy.exceedLimit(point, 4L, 4L));
            } else {
                assertTrue(strategy.exceedLimit(point, 4L, 4L));
            }
        }
    }
}

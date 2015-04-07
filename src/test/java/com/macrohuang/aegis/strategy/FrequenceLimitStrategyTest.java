/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.repository.CounterRepository;

/**
 * @title FrameLimitStrategyTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-7-4
 * @version 1.0
 */
public class FrequenceLimitStrategyTest {
    @Mock
    BlockedPoint point;
    @Mock
    CounterRepository counterRepository;
    int limit = 5;
    FrequencyLimitStrategy strategy = new FrequencyLimitStrategy(BlockedTimeType.Second, limit);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        strategy.setCounterRepository(counterRepository);

        when(point.getPointId()).thenReturn("quota-unit-test");
        when(counterRepository.getLastAccessTime(anyString()))
                .thenReturn(System.currentTimeMillis() - 2000)
                .thenThrow(new RuntimeException("Unit test exception"))
                .thenReturn(System.currentTimeMillis() - 100);

        doNothing().when(counterRepository).removeAccessRecord(anyString(), anyInt());
    }

    @Test
    public void testExceedLimitMinusLimit() {
        strategy = new FrequencyLimitStrategy(BlockedTimeType.Second, -1, 10000);
        assertNotNull(strategy);
        assertTrue(strategy.exceedLimit(point, 1L));
        strategy.setLimit(0L);
        assertTrue(strategy.exceedLimit(point, 1L));
    }

    @Test
    public void testExceedLimit() {
        assertFalse(strategy.exceedLimit(point, 1L));
        assertFalse(strategy.exceedLimit(point, 1L));
        assertTrue(strategy.exceedLimit(point, 2L));
    }

    @Test
    public void testExceedLimitException() {
        doThrow(new RuntimeException("Unit test exception")).when(counterRepository)
                .updateLastAccessTime(anyString(), anyLong());
        assertFalse(strategy.exceedLimit(point, 1L));
    }

    @Test
    public void testGetLimit() {
        assertEquals(limit, strategy.getLimit(1L));
    }
}

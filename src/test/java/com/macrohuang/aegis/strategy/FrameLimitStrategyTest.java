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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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
public class FrameLimitStrategyTest {
    @Mock
    BlockedPoint point;
    @Mock
    CounterRepository counterRepository;
    int limit = 5;
    FrameLimitStrategy strategy = new FrameLimitStrategy(BlockedTimeType.Second, limit);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        strategy.setCounterRepository(counterRepository);

        when(point.getPointId()).thenReturn("quota-unit-test");
        when(counterRepository.getAccessList(anyString())).thenReturn(
                Arrays.asList(System.currentTimeMillis() - 2000, System.currentTimeMillis() - 1000,
                        System.currentTimeMillis() - 500,
                        System.currentTimeMillis() - 250, System.currentTimeMillis() - 125))
                .thenThrow(new RuntimeException("Unit test exception"))
                .thenReturn(
                        Arrays.asList(System.currentTimeMillis() - 2000,
                                System.currentTimeMillis() - 1000,
                                System.currentTimeMillis() - 500, System.currentTimeMillis() - 250,
                                System.currentTimeMillis() - 125, System.currentTimeMillis() - 64,
                                System.currentTimeMillis() - 32, System.currentTimeMillis() - 16,
                                System.currentTimeMillis() - 8, System.currentTimeMillis() - 4,
                                System.currentTimeMillis() - 2, System.currentTimeMillis() - 1));

        doNothing().when(counterRepository).removeAccessRecord(anyString(), anyInt());
    }

    @Test
    public void testExceedLimitMinusLimit() {
        FrameLimitStrategy strategy2 = new FrameLimitStrategy();
        assertNotNull(strategy2);
        strategy2.setLimit(-1L);
        assertTrue(strategy2.exceedLimit(point, 1L));
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
                .removeAccessRecord(anyString(), anyInt());
        assertFalse(strategy.exceedLimit(point, 1L));
    }

    @Test
    public void testGetLimit() {
        assertEquals(limit, strategy.getLimit(1L));
    }
}

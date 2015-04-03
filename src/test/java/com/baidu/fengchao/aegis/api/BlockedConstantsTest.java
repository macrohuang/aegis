/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;

/**
 * @title BlockedConstantsTest
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class BlockedConstantsTest {
    @Test
    public void testFromTimeTypeChar() {
        BlockedTimeType timeType = BlockedConstants.BlockedTimeType.fromTypeChar('W');
        assertNotNull(timeType);
        assertEquals(BlockedTimeType.Week, timeType);

        timeType = BlockedConstants.BlockedTimeType.fromTypeChar('w');
        assertNotNull(timeType);
        assertEquals(BlockedTimeType.Week, timeType);

        timeType = BlockedConstants.BlockedTimeType.fromTypeChar('N');
        assertNotNull(timeType);
        assertEquals(BlockedTimeType.Minute, timeType);
    }
}

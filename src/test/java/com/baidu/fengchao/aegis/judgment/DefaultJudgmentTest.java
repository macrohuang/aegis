/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.judgment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.baidu.fengchao.aegis.judgement.DefaultJudgment;
import com.baidu.fengchao.aegis.judgement.Judgment;

/**
 * @title DefaultJudgmentTest
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class DefaultJudgmentTest {
    @Test
    public void testBlock() {
        Judgment judgment = new DefaultJudgment();
        assertNotNull(judgment);
        assertTrue(judgment.block("1"));
        assertTrue(judgment.block(null));
    }
}

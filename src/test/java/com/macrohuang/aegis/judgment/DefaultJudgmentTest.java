/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.judgment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.macrohuang.aegis.judgement.DefaultJudgment;
import com.macrohuang.aegis.judgement.Judgment;

/**
 * @title DefaultJudgmentTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
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

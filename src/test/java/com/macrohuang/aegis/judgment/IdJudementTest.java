/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.judgment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.macrohuang.aegis.api.BlockedConstants.BlockedType;
import com.macrohuang.aegis.judgement.IdJugdement;
import com.macrohuang.aegis.judgement.Judgment;

/**
 * @title IdJudementTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class IdJudementTest {
    @Test
    public void testBlockWhite() {
        Judgment judgment = new IdJugdement(BlockedType.White, new HashSet<String>(Arrays.asList(
                "1", "2")));
        assertNotNull(judgment);
        assertTrue(judgment.block("0"));
        assertTrue(judgment.block("3"));
        assertFalse(judgment.block("1"));
        assertFalse(judgment.block("2"));
    }

    @Test
    public void testBlockBlack() {
        Judgment judgment = new IdJugdement(BlockedType.Black, new HashSet<String>(Arrays.asList(
                "1", "2")));
        assertNotNull(judgment);
        assertTrue(judgment.block("1"));
        assertTrue(judgment.block("2"));
        assertFalse(judgment.block("0"));
        assertFalse(judgment.block("3"));
    }

}

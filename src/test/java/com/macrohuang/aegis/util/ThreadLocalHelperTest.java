package com.macrohuang.aegis.util;

import org.junit.Assert;
import org.junit.Test;

import com.macrohuang.aegis.util.ThreadLocalHelper;

public class ThreadLocalHelperTest {
    @Test
    public void testGetBlockKey() {
        Object[] key = new Object[] {};
        ThreadLocalHelper.bindBlockedKey(key);
        Assert.assertNotNull(ThreadLocalHelper.getBlockedKey());
        Assert.assertArrayEquals(key, ThreadLocalHelper.getBlockedKey());
    }
}

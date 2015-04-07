/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.quota;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.macrohuang.aegis.key.ArgIdxBlockedKey;
import com.macrohuang.aegis.key.BindingBlockedKey;
import com.macrohuang.aegis.util.ThreadLocalHelper;

/**
 * @title BaseQuotaTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class BaseQuotaTest {
    BaseQuota quota;

    @Test
    public void testGetQuota() {
        quota = new BaseQuota(10, new BindingBlockedKey());
        ThreadLocalHelper.bindBlockedKey(new Object[] { 1L });
        quota.addQuota(1L, 20);
        assertEquals(20, quota.getQuota(1L));
    }

    @Test
    public void testGetQuotaArgKey() {
        quota = new BaseQuota(10, new ArgIdxBlockedKey(0));
        quota.addQuota(1L, 20);
        assertEquals(20, quota.getQuota(1L));
        assertEquals(10, quota.getQuota(2L));
    }
}

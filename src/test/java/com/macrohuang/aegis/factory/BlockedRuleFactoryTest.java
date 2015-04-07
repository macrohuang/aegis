/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.bo.BlockedRule;
import com.macrohuang.aegis.factory.BlockedRuleFactory;

/**
 * @title BlockedRuleFactoryTest
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-7-4
 * @version 1.0
 */
public class BlockedRuleFactoryTest {
    String[] normalRule = new String[] {
            "a=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:1=3,2=8,3=10-Xtype:black-Xblock:1s-Xrepository:ramCounterRepository",
            "c=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.macrohuang.aegis.quota.CustomQuota",
            " =-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.macrohuang.aegis.quota.CustomQuota",
            "//d=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.macrohuang.aegis.quota.CustomQuota",
            "e=   ",
            "   ",
            null,
            "c=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:-X-X",
            "c=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.macrohuang.aegis.quota.CustomQuota-Xtest:test" };
    Map<String, BlockedPoint> pointById;
    @Mock
    ApplicationContext context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pointById = new HashMap<String, BlockedPoint>();
    }
    @Test
    public void testPaddingRules() {
        BlockedRuleFactory.paddingRules(context, normalRule, pointById);
        assertFalse(pointById.isEmpty());
    }

    @Test
    public void testEmpty() {

        BlockedRuleFactory.paddingRules(context, null, pointById);
        assertTrue(pointById.isEmpty());

        BlockedRuleFactory.paddingRules(context, new String[] {}, pointById);
        assertTrue(pointById.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOtherCondition() {
        new BlockedRuleFactory();
        BlockedRule.getAllSupportedArg().add("test");
        BlockedRuleFactory.paddingRules(context, normalRule, pointById);

    }
}

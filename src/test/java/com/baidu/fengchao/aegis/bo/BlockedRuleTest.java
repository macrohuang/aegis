/**
 * Baidu.com Inc. Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyInt;

import static org.mockito.Mockito.when;

import com.baidu.fengchao.aegis.key.ComboBlockedKey;
import com.baidu.fengchao.aegis.quota.Quota;
import com.baidu.fengchao.aegis.strategy.QuotaLimitStrategy;
import com.baidu.fengchao.aegis.strategy.TimeLimitStrategy;
import com.baidu.fengchao.skynet.factory.BlockedRuleFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

/**
 * @title BlockedRuleTest
 * @description TODO
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class BlockedRuleTest {
    String baseQuotaId = "skynet-unit-test1";
    String beanQuotaId = "skynet-unit-test2";
    String customQuotaId = "skynet-unit-test3";
    String errorQuotaId = "skynet-unit-test4";
    String multKeyId = "skynet-unit-test5";

    String[] rule = new String[] {
            baseQuotaId
                    + "=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:1=3,2=8,3=10-Xtype:black-Xblock:1s-Xrepository:ramCounterRepository",
            beanQuotaId + "=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:bean_myQuota",
            customQuotaId
                    + "=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.baidu.fengchao.aegis.quota.CustomQuota",
            errorQuotaId + "=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:bean_myQuotaNotExists", "  ",
            multKeyId + "=-Xlimit:6M-Xkey:arg[0],arg[1].name,arg[2]" };

    Map<String, BlockedPoint> pointById = new HashMap<String, BlockedPoint>();
    @Mock
    Quota myQuota;
    @Mock
    ApplicationContext context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.containsBean("myQuota")).thenReturn(true);
        when(context.getBean("myQuota")).thenReturn(myQuota);
        when(myQuota.getQuota()).thenReturn(5);
        when(myQuota.getQuota(anyInt())).thenReturn(8);

        BlockedRuleFactory.paddingRules(context, rule, pointById);
        assertFalse(pointById.isEmpty());
    }

    @Test
    public void testBuildRuleBaseQuota() {
        BlockedPoint point = pointById.get(baseQuotaId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        assertNotNull(rule.getLimitStrategy());
        assertTrue(rule.getLimitStrategy() instanceof QuotaLimitStrategy);
        QuotaLimitStrategy strategy = (QuotaLimitStrategy) rule.getLimitStrategy();
        Quota quota = strategy.getQuota();
        assertNotNull(quota);
        assertEquals(3, quota.getQuota("1"));
        assertEquals(8, quota.getQuota("2"));
        assertEquals(10, quota.getQuota("3"));
        assertEquals(6, quota.getQuota("4"));
    }

    @Test
    public void testBuildRuleCustomQuota() {
        BlockedPoint point = pointById.get(customQuotaId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        assertNotNull(rule.getLimitStrategy());
        assertTrue(rule.getLimitStrategy() instanceof QuotaLimitStrategy);
        QuotaLimitStrategy strategy = (QuotaLimitStrategy) rule.getLimitStrategy();
        Quota quota = strategy.getQuota();
        assertNotNull(quota);
        assertEquals(5, quota.getQuota());
        assertEquals(10, quota.getQuota("3"));
        assertEquals(10, quota.getQuota("4"));
    }

    @Test
    public void testBuildRuleBeanQuota() {
        BlockedPoint point = pointById.get(beanQuotaId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        assertNotNull(rule.getLimitStrategy());
        assertTrue(rule.getLimitStrategy() instanceof QuotaLimitStrategy);
        QuotaLimitStrategy strategy = (QuotaLimitStrategy) rule.getLimitStrategy();
        Quota quota = strategy.getQuota();
        assertNotNull(quota);
        assertEquals(5, quota.getQuota());
        assertEquals(8, quota.getQuota(1));
        assertEquals(8, quota.getQuota(12));
        assertEquals(8, quota.getQuota(123));
    }

    @Test
    public void testBuildRuleBeanQuotaException() {
        BlockedPoint point = pointById.get(errorQuotaId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        assertNotNull(rule.getLimitStrategy());
        assertTrue(rule.getLimitStrategy() instanceof TimeLimitStrategy);
    }

    @Test
    public void testEquals() {
        BlockedPoint point = pointById.get(baseQuotaId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        assertTrue(rule.equals(rule));
    }

    @Test
    public void testMultKeys() {
        BlockedPoint point = pointById.get(multKeyId);
        assertNotNull(point);
        assertNotNull(point.getRules());
        assertFalse(point.getRules().isEmpty());
        BlockedRule rule = point.getRules().get(0);
        assertNotNull(rule);
        class MyObj {
            @SuppressWarnings("unused")
            private String name;
            
            public MyObj(String name){
                this.name = name;
            }
        }
        ComboBlockedKey comboBlockedKey = (ComboBlockedKey) rule.getBlockedKey();
        assertNotNull(comboBlockedKey);
        Object[] keys = comboBlockedKey.getBlockedKeys("Hello", new MyObj("world"), "!");
        assertNotNull(keys);
        System.out.println(Arrays.toString(keys));
        assertTrue(keys.length > 0);
        assertEquals("Hello.world.!", keys[0]);
    }
}

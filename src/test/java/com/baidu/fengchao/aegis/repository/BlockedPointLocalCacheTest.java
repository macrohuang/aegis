/**
 * Baidu.com Inc. Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.baidu.fengchao.aegis.api.BlockedRuleChangedCallback;
import com.baidu.fengchao.ether.api.IConfCenterClient;
import com.baidu.fengchao.skynet.repository.BlockedPointLocalCache;

/**
 * @title BlockedPointLocalCacheTest
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-7-4
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockedPointLocalCacheTest {
    BlockedPointLocalCache pointLocalCache = new BlockedPointLocalCache();
    @Mock
    IConfCenterClient confCenterClient;
    @Mock
    ApplicationContext context;

    AtomicBoolean change = new AtomicBoolean(false);
    BlockedRuleChangedCallback callback;
    @Mock
    BlockedRuleChangedCallback beanCallback;

    @Before
    public void init() {
        callback = new BlockedRuleChangedCallback() {

            @Override
            public void ruleChanged() {
                change.set(true);

            }
        };
        when(confCenterClient.getConfFileAll(anyString()))
                .thenReturn(
                        "a=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:1=3,2=8,3=10-Xtype:black-Xblock:1s-Xrepository:ramCounterRepository\n"
                                + "c=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.baidu.fengchao.aegis.quota.CustomQuota\n"
                                + " =-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.baidu.fengchao.aegis.quota.CustomQuota\n"
                                + "//d=-Xlimit:6M-Xcluster:false-Xstrategy:quota-Xquota:com.baidu.fengchao.aegis.quota.CustomQuota");
        when(context.getBeansOfType(BlockedRuleChangedCallback.class)).thenReturn(
                new HashMap<String, BlockedRuleChangedCallback>() {
                    /**
                    * 
                    */
                    private static final long serialVersionUID = -1496906391448600069L;

                    {
                        put("callback", beanCallback);
        }});
        
        pointLocalCache.setConfCenterClient(confCenterClient);
        pointLocalCache.setConfCenterClient(confCenterClient);
        pointLocalCache.setApplicationContext(context);
        BlockedPointLocalCache.addCallback(callback);
    }

    @Test
    public void testFileChanged() {
        assertFalse(change.get());
        pointLocalCache.fileChanged("from unit test");
        assertTrue(change.get());
        change.set(false);
        assertFalse(change.get());
        BlockedPointLocalCache.removeCallback(callback);
        pointLocalCache.fileChanged("from unit test");
        assertFalse(change.get());
    }
}

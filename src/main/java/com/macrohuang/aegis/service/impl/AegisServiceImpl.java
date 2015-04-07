/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.macrohuang.aegis.api.BlockedRuleChangedCallback;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.bo.BlockedRule;
import com.macrohuang.aegis.exception.TooFrequentInvokeException;
import com.macrohuang.aegis.repository.BlockedPointLocalCache;
import com.macrohuang.aegis.service.AegisService;

/**
 * @title SkynetServiceImpl
 * @description TODO 
 * @author work
 * @date 2014-3-3
 * @version 1.0
 */
public class AegisServiceImpl implements AegisService {

    private Lock lock = new ReentrantLock();
    private static final Logger logger = LoggerFactory.getLogger(AegisServiceImpl.class);
    private BlockedPointLocalCache pointLocalCache;
    /* (non-Javadoc)
     * @see com.macrohuang.skynet.service.SkynetService#check(java.lang.Object[])
     */
    @Override
    public Object check(Object... params) throws TooFrequentInvokeException {
        int depth = Thread.currentThread().getStackTrace().length;
        if (depth > 2) {
            String blockedPointID = Thread.currentThread().getStackTrace()[2].getClassName() + "."
                    + Thread.currentThread().getStackTrace()[2].getMethodName();
            return check(blockedPointID, params);
        }
        return null;
    }

    protected Object check(String pointId, Object... params) throws TooFrequentInvokeException {
        BlockedPoint blockedPoint = getPointLocalCache().getBlockedPoint(pointId);
        if (blockedPoint != null) {
            for (BlockedRule rule : blockedPoint.getRules()) {
                List<Object> exceedKeys = new ArrayList<Object>();
                for (Object key : rule.getBlockedKey().getBlockedKeys(params)) {
                    if (rule.getJudgement().block(key, params)) {
                        try {
                            if (rule.isStrict()) {
                                lock.lock();
                            }
                            if (rule.getLimitStrategy().isExceedLimit(blockedPoint, key, params)) {
                                logger.info("AEGIS SKYNET blocks [" + key + " ], strategy=" + rule.getLimitStrategy()
                                        + ",limit=" + rule.getLimit() + ", policy=" + rule.getPolicy());
                                exceedKeys.add(key);
                            }
                        } finally {
                            if (rule.isStrict())
                                lock.unlock();
                        }
                    }
                }
                if (!exceedKeys.isEmpty()) {
                    throw new TooFrequentInvokeException(rule.getPolicy().getResult(exceedKeys.toArray(), params));
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.macrohuang.skynet.service.SkynetService#addRuleChangedCallback(com.macrohuang.aegis.api.BlockedRuleChangedCallback)
     */
    @Override
    public void addRuleChangedCallback(BlockedRuleChangedCallback callback) {
        BlockedPointLocalCache.addCallback(callback);
    }

    /* (non-Javadoc)
     * @see com.macrohuang.skynet.service.SkynetService#revomeRuleChangedCallback(com.macrohuang.aegis.api.BlockedRuleChangedCallback)
     */
    @Override
    public void revomeRuleChangedCallback(BlockedRuleChangedCallback callback) {
        BlockedPointLocalCache.removeCallback(callback);
    }

    /**
     * @return the pointLocalCache
     */
    public BlockedPointLocalCache getPointLocalCache() {
        return pointLocalCache;
    }

    /**
     * @param pointLocalCache the pointLocalCache to set
     */
    public void setPointLocalCache(BlockedPointLocalCache pointLocalCache) {
        this.pointLocalCache = pointLocalCache;
    }
}

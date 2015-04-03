/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;

/**
 * @title CustomQuotaStrategy
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class CustomQuotaStrategy extends QuotaLimitStrategy {

    /**
     * @param timeType
     * @param limit
     */
    public CustomQuotaStrategy(BlockedTimeType timeType, int limit) {
        super(timeType, limit);
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.aegis.strategy.QuotaLimitStrategy#getDescQuota(java.lang.Object[])
     */
    @Override
    protected int getDescQuota(Object... params) {
        return 2;
    }
}

/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.strategy;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.quota.Quota;

/**
 * @title QuotaLimitStrategy
 * @description 根据配额的封禁策略 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class QuotaLimitStrategy extends TimeLimitStrategy {

    private Quota quota;

    public QuotaLimitStrategy(BlockedTimeType timeType, int limit) {
        super(timeType, limit);
    }

    public QuotaLimitStrategy(BlockedTimeType timeType, int limit, int block) {
        super(timeType, limit, block);
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.aegis.strategy.AbstractLimitStrategy#getLimit(java.lang.Object[])
     */
    @Override
    public long getLimit(Object... params) {
        return quota == null ? super.getLimit(params) : quota.getQuota(params);
    }

    /**
     * @return the quota
     */
    public Quota getQuota() {
        return quota;
    }

    /**
     * @param quota the quota to set
     */
    public void setQuota(Quota quota) {
        this.quota = quota;
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.aegis.strategy.TimeLimitStrategy#getAccessCount(java.lang.Object[])
     */
    @Override
    protected int getAccessCount(Object... params) {
        return getDescQuota(params);
    }

    /**
     * 一次成功请求后减少的quota
     * @param params 请求的参数
     * @return 请求结束后被减少的quota，默认为1
     */
    protected int getDescQuota(Object... params) {
        return 1;
    }
}

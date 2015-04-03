/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.quota;

/**
 * @title Quota
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-6-26
 * @version 1.0
 */
public interface Quota {
    /**
     * 获取实体的配额
     * @param params
     * @return 实体的配额
     */
    int getQuota(Object... params);
}

/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.quota;

/**
 * @title Quota
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
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

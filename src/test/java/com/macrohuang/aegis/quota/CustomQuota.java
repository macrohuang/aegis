/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.quota;

/**
 * @title CustomQuota
 * @description TODO 
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class CustomQuota implements Quota {

    /* (non-Javadoc)
     * @see com.macrohuang.aegis.quota.Quota#getQuota(java.lang.Object[])
     */
    @Override
    public int getQuota(Object... params) {
        if (params == null || params.length == 0) {
            return 5;
        } else {
            return 10;
        }
    }
}

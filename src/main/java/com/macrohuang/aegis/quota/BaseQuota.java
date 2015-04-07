/**
 * Macro Huang  Copyright(c) 2014-2015 All Rights Reserved.
 */
package com.macrohuang.aegis.quota;

import java.util.HashMap;
import java.util.Map;

import com.macrohuang.aegis.key.BlockedKey;

/**
 * @title BaseQuota
 * @description 根据BlockedKey中第一个Key获取对应的quota，如果没有设置，则返回limit中设置的值作为quota
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-6-26
 * @version 1.0
 */
public class BaseQuota implements Quota {

    private int quota;
    private BlockedKey key;
    private Map<Object, Integer> quotaByKey = new HashMap<Object, Integer>();

    public BaseQuota(int quota, BlockedKey key) {
        this.quota = quota;
        this.key = key;
    }

    /**
     * 增加单个实体的quota
     * @param entity 所要增加quota的实体主键，一般为userid
     * @param quota 该实体的quota
     * @return
     */
    public BaseQuota addQuota(Object entity, int quota) {
        quotaByKey.put(entity, quota);
        return this;
    }

    /* (non-Javadoc)
     * @see com.macrohuang.aegis.quota.Quota#getQuota(java.lang.Object[])
     */
    @Override
    public int getQuota(Object... params) {
        if (key == null || params == null || params.length == 0) {
            return quota;
        } else {
            Object k = key.getBlockedKeys(params)[0];
            if (quotaByKey.containsKey(k)) {
                return quotaByKey.get(k);
            } else {
                return quota;
            }
        }
    }
}

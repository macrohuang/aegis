/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.key;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * @title ComboBlockedKey
 * @description 组合的block key，支持多个参数或参数属性的组合
 * @author huangtianlai01(haungtianlai01@baidu.com)
 * @date 2014-9-2
 * @version 1.0
 */
public class ComboBlockedKey implements BlockedKey {
    Map<Integer, String> propByIdx = new LinkedHashMap<Integer, String>();
    private Map<String, Field> fieldCache = new ConcurrentHashMap<String, Field>();
    private Logger logger = Logger.getLogger(ComboBlockedKey.class);

    public void add(Integer idx, String prop) {
        propByIdx.put(idx, prop);
    }

    public void add(Integer idx) {
        propByIdx.put(idx, "");
    }

    /* (non-Javadoc)
     * @see com.baidu.fengchao.aegis.key.BlockedKey#getBlockedKeys(java.lang.Object[])
     */
    @Override
    public Object[] getBlockedKeys(Object... params) {
        Assert.notNull(params);
        StringBuilder sb = new StringBuilder();
        for (Integer idx : propByIdx.keySet()) {
            Assert.isTrue(params.length > idx);
            if (StringUtils.isNotBlank(propByIdx.get(idx))) {
                Field field = null;
                if (!fieldCache.containsKey(propByIdx.get(idx))) {
                    try {
                        field = params[idx].getClass().getDeclaredField(propByIdx.get(idx));
                        field.setAccessible(true);
                        fieldCache.put(propByIdx.get(idx), field);
                    } catch (Exception e) {
                        logger.error("Error while get block key", e);
                    }
                }
                try {
                    field = fieldCache.get(propByIdx.get(idx));
                    sb.append(field.get(params[idx])).append(".");
                } catch (Exception e) {
                    logger.error("Error while get block key", e);
                }
            } else {
                sb.append(params[idx]).append(".");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return new Object[] { sb.toString() };
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

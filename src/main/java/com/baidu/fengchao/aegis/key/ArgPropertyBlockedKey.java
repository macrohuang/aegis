package com.baidu.fengchao.aegis.key;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;

/**
 * Consider the method's <code>N</code>th argument's property<code>prop</code>
 * as the invoke key.
 * 
 * @author Macro Huang
 * 
 */
public class ArgPropertyBlockedKey implements BlockedKey {
    private int idx;
    private String prop;
    private Map<String, Field> fieldCache = new ConcurrentHashMap<String, Field>();

    public ArgPropertyBlockedKey(int idx, String prop) {
        this.idx = idx;
        this.prop = prop;
    }

    private String getFieldCacheKey(Object param) {
        return param.getClass().getName() + "." + prop;
    }

    @Override
    public Object[] getBlockedKeys(Object... params) {
        Assert.notNull(params);
        Assert.isTrue(params.length > idx);
        Object param = params[idx];
        try {
            Field field = null;
            if (fieldCache.containsKey(getFieldCacheKey(param))) {
                field = fieldCache.get(getFieldCacheKey(param));
            } else {
                field = param.getClass().getDeclaredField(prop);
                fieldCache.put(getFieldCacheKey(param), field);
            }
            field.setAccessible(true);
            return new Object[] { field.get(param) };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

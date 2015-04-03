package com.baidu.fengchao.aegis.key;

import org.springframework.util.Assert;

/**
 * Consider the method's <code>N</code>th argument as the invoke key.
 * 
 * @author Macro Huang
 * 
 */
public class ArgIdxBlockedKey implements BlockedKey {
    private int idx;

    public ArgIdxBlockedKey(int idx) {
        this.idx = idx;
    }

    @Override
    public Object[] getBlockedKeys(Object... params) {
        Assert.notNull(params);
        Assert.isTrue(params.length > idx);
        return new Object[] { params[idx] };
    }

    @Override
    public String toString() {
        return "ArgIdxBlockedKey [idx=" + idx + "]";
    }
}

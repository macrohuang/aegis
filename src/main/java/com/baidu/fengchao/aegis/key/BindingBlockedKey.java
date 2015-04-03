package com.baidu.fengchao.aegis.key;

import com.baidu.fengchao.skynet.util.ThreadLocalHelper;

/**
 * Use the binding key in ThreadLocalHelper.bindingBlockedKey(Object obj).
 * 
 * @author Macro Huang
 * 
 */
public class BindingBlockedKey implements BlockedKey {

    @Override
    public Object[] getBlockedKeys(Object... params) {
        return ThreadLocalHelper.getBlockedKey();
    }
}

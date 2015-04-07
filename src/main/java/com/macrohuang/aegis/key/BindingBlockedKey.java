package com.macrohuang.aegis.key;

import com.macrohuang.aegis.util.ThreadLocalHelper;

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

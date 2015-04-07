package com.macrohuang.aegis.policy;

import com.macrohuang.aegis.exception.TooFrequentInvokeException;

/**
 * @author Macro Huang
 * 
 */
public interface Policy {
    public Object getResult(Object[] exceedKeys, Object... params) throws TooFrequentInvokeException;
}
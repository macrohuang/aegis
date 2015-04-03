package com.baidu.fengchao.aegis.policy;

import com.baidu.fengchao.aegis.exception.TooFrequentInvokeException;

/**
 * @author Macro Huang
 * 
 */
public interface Policy {
    public Object getResult(Object[] exceedKeys, Object... params) throws TooFrequentInvokeException;
}
package com.macrohuang.aegis.policy;


/**
 * Return <code>null</code> while exceed the limit invoke.
 * 
 * @author Macro Huang
 * 
 */
public class NullPolicy implements Policy {

    @Override
    public Object getResult(Object[] exceedKeys, Object... params) {
        return null;
    }
}

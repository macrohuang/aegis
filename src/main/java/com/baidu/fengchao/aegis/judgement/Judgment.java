package com.baidu.fengchao.aegis.judgement;


/**
 * 
 * @author Macro Huang
 * 
 */
public interface Judgment {
    public abstract boolean block(Object key, Object... params);
}

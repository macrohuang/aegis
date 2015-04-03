package com.baidu.fengchao.aegis.judgement;


/**
 * The default judgment will consider every invoke should be checked.
 * @author work
 * 
 */
public class DefaultJudgment implements Judgment {

    @Override
    public boolean block(Object key, Object... params) {
        return true;
    }
}

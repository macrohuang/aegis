package com.baidu.fengchao.aegis.judgement;

import java.util.Set;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedType;

/**
 * IdJudgment will see if the invoke key is in the id list. If blocked type is
 * white list, then if the invoke key is in the id list, it will not be checked;
 * if the blocked type is black list, then only the invoke key in the list will
 * be checked.
 * 
 * @author Macro Huang
 * 
 */
public class IdJugdement implements Judgment {
    private BlockedType blockedType;
    private Set<String> ids;

    public IdJugdement(BlockedType blockedType, Set<String> ids) {
        this.blockedType = blockedType;
        this.ids = ids;
    }

    @Override
    public boolean block(Object key, Object... params) {
        return (blockedType == BlockedType.Black && ids.contains(String.valueOf(key)) || blockedType == BlockedType.White
                && !ids.contains(String.valueOf(key)));
    }

    @Override
    public String toString() {
        return "IdJugdement [blockedType=" + blockedType + ", ids=" + ids + "]";
    }
}

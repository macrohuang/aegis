/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.bo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockedPoint implements java.io.Serializable {
    private static final long serialVersionUID = 2731364998416229792L;

    private List<BlockedRule> rules;
    private Map<String, String> params;
    private String pointId;

    public BlockedPoint(String pointId) {
        this.pointId = pointId;
        this.rules = new CopyOnWriteArrayList<BlockedRule>();
        this.params = new ConcurrentHashMap<String, String>();
    }

    public void clearRules() {
        this.rules.clear();
    }

    /**
     * 
     * @param newRule
     */
    public void addRule(BlockedRule newRule) {
        this.rules.add(newRule);
    }

    public void putParam(String key, String value) {
        this.params.put(key, value);
    }

    public Map<String, String> getParams() {
        return params;
    }

    /**
     * @return the pointId
     */
    public String getPointId() {
        return pointId;
    }

    /**
     * Getter method for property <tt>rules</tt>.
     * 
     * @return property value of rules
     */
    public List<BlockedRule> getRules() {
        return this.rules;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlockedPoint other = (BlockedPoint) obj;
        if (rules == null) {
            if (other.rules != null)
                return false;
        } else if (!rules.equals(other.rules))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BlockedPoint [rules=" + rules + "]";
    }
}

/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.service;

import com.macrohuang.aegis.api.BlockedRuleChangedCallback;
import com.macrohuang.aegis.exception.TooFrequentInvokeException;

/**
 * @title SkynetService
 * @description TODO 
 * @author work
 * @date 2014-3-3
 * @version 1.0
 */
public interface AegisService {
    Object check(Object... params) throws TooFrequentInvokeException;

    void addRuleChangedCallback(BlockedRuleChangedCallback callback);

    void revomeRuleChangedCallback(BlockedRuleChangedCallback callback);
}

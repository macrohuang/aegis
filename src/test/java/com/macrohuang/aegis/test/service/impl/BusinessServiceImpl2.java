/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.test.service.impl;

import java.util.List;

import com.macrohuang.aegis.exception.TooFrequentInvokeException;
import com.macrohuang.aegis.service.AegisService;

/**
 * @title BusinessServiceImpl2
 * @description TODO 
 * @author work
 * @date 2014-3-3
 * @version 1.0
 */
public class BusinessServiceImpl2 extends BusinessServiceImpl {
    private AegisService aegisService;
    /* (non-Javadoc)
     * @see com.macrohuang.aegis.test.service.impl.BusinessServiceImpl#say(long, java.lang.String, java.util.List)
     */
    @Override
    public String say(long userID, String userName, List<String> somebody) {
        try {
            aegisService.check(userID, userName, somebody);
        } catch (TooFrequentInvokeException e) {
            return (String) e.getResult();
        }
        return super.say(userID, userName, somebody);
    }

    /**
     * @param aegisService the aegisService to set
     */
    public void setAegisService(AegisService aegisService) {
        this.aegisService = aegisService;
    }
}

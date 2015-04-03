/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.test.service.impl;

import java.util.List;

import com.baidu.fengchao.aegis.exception.TooFrequentInvokeException;
import com.baidu.fengchao.skynet.service.SkynetService;

/**
 * @title BusinessServiceImpl2
 * @description TODO 
 * @author work
 * @date 2014-3-3
 * @version 1.0
 */
public class BusinessServiceImpl2 extends BusinessServiceImpl {
    private SkynetService skynetService;
    /* (non-Javadoc)
     * @see com.baidu.fengchao.aegis.test.service.impl.BusinessServiceImpl#say(long, java.lang.String, java.util.List)
     */
    @Override
    public String say(long userID, String userName, List<String> somebody) {
        try {
            skynetService.check(userID, userName, somebody);
        } catch (TooFrequentInvokeException e) {
            return (String) e.getResult();
        }
        return super.say(userID, userName, somebody);
    }

    /**
     * @param skynetService the skynetService to set
     */
    public void setSkynetService(SkynetService skynetService) {
        this.skynetService = skynetService;
    }
}

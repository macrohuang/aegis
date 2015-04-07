/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.macrohuang.aegis.test.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.macrohuang.aegis.test.service.BusinessService;

/**
 * @title SkynetServiceTest
 * @description TODO 
 * @author work
 * @date 2014-3-3
 * @version 1.0
 */
public class AegisServiceTest {
    private static int COUNT = 10000;
    private BusinessService businessService;
    private BusinessService businessNoAop;
    private static final long userID = 112L;

    @Before
    public void init() {
        ApplicationContext aContext = new ClassPathXmlApplicationContext("blocked-aop.xml");
        ApplicationContext bContext = new ClassPathXmlApplicationContext("blocked-base.xml");
        businessService = (BusinessService) aContext.getBean("businessService");
        businessNoAop = (BusinessService) bContext.getBean("businessService2");
    }

    @Test
    public void testSayCompareAopAndNoAop() {
        List<String> somebody = new ArrayList<String>();
        somebody.add("zhansan");
        somebody.add("lisi");
        somebody.add("wangwu");
        System.out.println(businessService.say(userID, "ASUKA", somebody));
        System.out.println(businessNoAop.say(userID + 1, "ASUKA", somebody));
        System.out.println(businessService.say(userID, "ASUKA", somebody));
        System.out.println(businessNoAop.say(userID + 1, "ASUKA", somebody));
        System.out.println(businessService.say(userID, "ASUKA", somebody));
        System.out.println(businessNoAop.say(userID + 1, "ASUKA", somebody));
        long t = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            businessService.say(userID, "ASUKA", somebody);
        }
        long t1 = System.currentTimeMillis() - t;

        t = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            businessNoAop.say(userID + 1, "ASUKA", somebody);
        }
        System.out.println(String.format("call say without aop %d times cost %d", COUNT,
                (System.currentTimeMillis() - t)) + String.format("\ncall say with aop %d times cost %d", COUNT, (t1)));
    }
}

/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.macrohuang.aegis.test.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.macrohuang.aegis.test.service.BusinessService;
import com.macrohuang.aegis.test.service.ThirdInterface;

public class PerformanceTest {

	private static int COUNT = 10;
	private BusinessService businessService;
	private BusinessService businessServiceNoLimit;
	private static final long userID = 112L;
    ApplicationContext aContext = new ClassPathXmlApplicationContext("blocked-aop.xml");
    ApplicationContext bContext = new ClassPathXmlApplicationContext("blocked-base.xml");
	@Before
	public void init() {
		businessService = (BusinessService) aContext.getBean("businessService");
		businessServiceNoLimit = (BusinessService) bContext.getBean("businessService");
	}

	@Test
	public void testSay() {
		List<String> somebody = new ArrayList<String>();
		somebody.add("zhansan");
		somebody.add("lisi");
		somebody.add("wangwu");
		long t = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			businessServiceNoLimit.say(i, "ASUKA", somebody);
		}
		System.out.println(String.format("call say without block %d times cost %d", COUNT,
				(System.currentTimeMillis() - t)));
		t = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			businessService.say(userID, "ASUKA", somebody);
		}
		System.out.println(String.format("call say with block %d times cost %d", COUNT,
				(System.currentTimeMillis() - t)));
	}

	public static void main(String[] args) {
		PerformanceTest performanceTest = new PerformanceTest();
		performanceTest.init();
		PerformanceTest.COUNT = 10000;
		performanceTest.testSay();
	}

	@Test
	public void testConfCenter() throws InterruptedException {
		// while (true) {
			System.out.println("I am alive!");
			TimeUnit.SECONDS.sleep(2);
		// }
	}

	@Test
	public void testLoop() {
		businessService.loopCall(3);
	}

	@Test
	public void testThirdService() {
		((ThirdInterface) businessService).work();
	}
}

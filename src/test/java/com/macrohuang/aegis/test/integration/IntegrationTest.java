/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.macrohuang.aegis.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.api.BlockedConstants.BlockedType;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.exception.TooFrequentInvokeException;
import com.macrohuang.aegis.test.service.BusinessService;
import com.macrohuang.aegis.test.service.BusinessService.MyUser;
import com.macrohuang.aegis.test.service.ModWordRequest;
import com.macrohuang.aegis.util.ThreadLocalHelper;

public class IntegrationTest {

	private static final int COUNT = 10;
	private BusinessService businessService;
	private static final long userID = 112L;
	private static final String userName = "Zhangsan";
    private ApplicationContext context;
	@Before
	public void init() {
        context = new ClassPathXmlApplicationContext("blocked-aop.xml");
        businessService = (BusinessService) context.getBean("businessService");
	}

	@Test
	public void testSay() {
		List<String> somebody = new ArrayList<String>();
		somebody.add("zhansan");
		somebody.add("lisi");
		somebody.add("wangwu");
		boolean hasBlocked = false;
		for (int i = 0; i < COUNT; i++) {
			String result = businessService.say(10003, "ASUKA", somebody);
			if (result == null) {
				System.out.println("is blocking !!!");
				hasBlocked = true;
			} else {
				System.out.println(result);
			}
		}
        assertTrue(hasBlocked);
	}

	@Test(expected = TooFrequentInvokeException.class)
	public void testAddNewWords() {
		for (int i = 0; i < COUNT; i++) {
			businessService.addNewWords(userID, userName,
					Arrays.asList(new Object[] { System.currentTimeMillis(), System.currentTimeMillis() }));
		}
	}

	@Test
	public void testQueryWordList() {
		MyUser myUser = new MyUser();
		myUser.userId = userID;
		boolean hasExceed = false;
		for (int i = 0; i < COUNT; i++) {
			List<String> list = businessService.queryWordList(myUser, userName, Arrays.asList("1001", "1002", "1003"));
			System.out.println(list);
			if (list.size() == 1) {
				hasExceed = true;
			}
		}
        assertTrue(hasExceed);
	}

	@Test
	public void testTest() {
		Pattern pattern = Pattern.compile("com.macrohuang.aegis.test.service.*Service*.*");
		System.out
				.println(pattern.matcher("com.macrohuang.aegis.test.service.impl.BusinessServiceImpl.say").find());

		pattern = Pattern.compile("com.macrohuang.aegis.test.service.*Service*.addNewWords");
		System.out.println(pattern
				.matcher("com.macrohuang.aegis.test.service.impl.BusinessServiceImpl.addNewWords").find());

		pattern = Pattern.compile("^com.macrohuang.*.service.*Service.*.hello$");
		System.out.println(pattern.matcher("com.macrohuang.aegis.test.service.impl.BusinessServiceImpl.hello2")
				.find());

		PathMatcher pathMatcher = new AntPathMatcher();
		System.out.println(pathMatcher.match("com.macrohuang.aegis.test.service.*Service*.addNewWords",
				"com.macrohuang.aegis.test.service.impl.BusinessServiceImpl.addNewWords"));
	}

	@Test
	public void testModWord() throws InterruptedException {
		ModWordRequest request = new ModWordRequest();
		Set<Long> winfoids = new HashSet<Long>();
		winfoids.add(1111L);
		winfoids.add(1112L);
		winfoids.add(1113L);
		winfoids.add(1114L);
		winfoids.add(1115L);
		boolean exceed = false;
		request.setWinfoid(winfoids);
		for (int i = 0; i < COUNT; i++) {
			winfoids.add(System.nanoTime());
			int status = businessService.modWordinfo(userID, request).getStatus();
			System.out.println(status);
			if (status == 500) {
				exceed = true;
				Thread.sleep(1000);
			}
		}
        assertTrue(exceed);
	}
	
	@Test
	public void testCustomBeanPolicy() {
		for (int i = 0; i < COUNT; i++) {
            System.out.println(businessService.hello(1234, "张三"));
		}
	}

	@Test
	public void testCustomBeanIds() {
		boolean exceed = false;
		ThreadLocalHelper.bindBlockedKey(new Integer[] { 1234 });
		for (int i = 0; i < COUNT; i++) {
            String result = businessService.hello2(1234, "张三");
			if (CustomBeanPolicy.s.equals(result)) {
				exceed = true;
			}
			System.out.println(result);
		}
        assertTrue(exceed);
	}

	@Test
	public void testExtraParams() {
		businessService.say(1234L, "abc", Arrays.asList("bc", "cd"));
		System.out.println(ThreadLocalHelper.getParams());
        assertNotNull(ThreadLocalHelper.getParamByKey("key1"));
        assertEquals("value1", ThreadLocalHelper.getParamByKey("key1"));
	}

    @Test
    public void testCover() {
        System.out.println(BlockedTimeType.fromTypeChar('e'));
        System.out.println(BlockedType.Black.getBlockedTypeChar());
        System.out.println(BlockedType.Black.fromChar('a'));
        System.out.println(BlockedType.Black.fromChar('B'));
        BlockedPoint point = new BlockedPoint("point_id");
        point.clearRules();
        System.out.println(point.getRules());
        System.out.println(point.hashCode());
        System.out.println(point.equals(this));
        System.out.println(point.equals(null));
        System.out.println(point.equals(point));
        System.out.println(point.equals(new BlockedPoint("point_id")));
        System.out.println(point.equals(new String()));

    }
}

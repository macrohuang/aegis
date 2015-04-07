/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.macrohuang.aegis.test.service;

import java.util.List;

/**
 * 
 * @author http://jiaqing.me
 * @version $Id: BusinessService.java, v 0.1 2012-11-29 ����11:37:18 zhengjiaqing Exp $
 */
public interface BusinessService {
	public static class MyUser {
		public long userId;
	}
    public String say(long userID, String userName, List<String> something);

	public List<String> queryWordList(MyUser userID, String userName, List<String> idList);

    public boolean addNewWords(long userID, String userName, List<Object> words);

	public WebBatchModResponse modWordinfo(Long userid, ModWordRequest request);

	public String hello(long userId, String userName);

	public String hello2(int i, String string);

	public void loopCall(int i);
}

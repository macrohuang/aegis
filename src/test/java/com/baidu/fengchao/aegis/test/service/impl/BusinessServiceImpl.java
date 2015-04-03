/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2012 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.test.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.fengchao.aegis.test.service.AnotherService;
import com.baidu.fengchao.aegis.test.service.BusinessService;
import com.baidu.fengchao.aegis.test.service.ModWordRequest;
import com.baidu.fengchao.aegis.test.service.ThirdInterface;
import com.baidu.fengchao.aegis.test.service.WebBatchModResponse;

/**
 * 
 * @author http://jiaqing.me
 * @version $Id: BusinessServiceImpl.java, v 0.1 2012-11-29 ����2:21:08 zhengjiaqing Exp $
 */
public class BusinessServiceImpl implements BusinessService, ThirdInterface {
    public String say(long userID, String userName, List<String> somebody) {
       return sayInner(userID,userName,somebody);
    }

    private String sayInner(long userID, String userName, List<String> somebody){
    	StringBuilder sb = new StringBuilder();
        sb.append(userName);
        sb.append(" say hello to ");
        for (String name : somebody) {
            sb.append(name + ", ");
        }
        return sb.toString();
    }

    public void sayBlockedCallback(long userID, String userName, List<String> somebody, List<String> params) {
        System.out.println("---- IN sayBlockedCallback()");
    }

	public List<String> queryWordList(MyUser userID, String userName, List<String> idList) {
        List<String> list = new ArrayList<String>();
		list.addAll(idList);
        return list;
    }

    protected long queryWordListBlockedCallback(long userID, String userName, List<String> idList,
            List<String> checkParams) {
        if (idList.containsAll(checkParams)) {
            System.out.println("IT CONTAINS ALLLLLLLLLLLLLLLLLLLLLLL");
            return idList.size() * 99L;
        } else {
            return 0L;
        }
    }

    // ~~~

    public boolean addNewWords(long userID, String userName, List<Object> words) {
        return true;
    }

    long addNewWordsBlockedCallback(long userID, String userName, List<Object> words, List<String> checkParams) {

        return 0L;
    }

	public WebBatchModResponse modWordinfo(Long userid, ModWordRequest request) {
		WebBatchModResponse response = new WebBatchModResponse();
		response.setStatus(200);
		return response;
	}

	@Override
	public String hello(long userId, String userName) {
		return userName + " 's id is: " + userId;
	}

	@Override
	public String hello2(int i, String string) {
		return i + " 's id is: " + string;
	}

	private AnotherService anotherService;

	public AnotherService getAnotherService() {
		return anotherService;
	}

	public void setAnotherService(AnotherService anotherService) {
		this.anotherService = anotherService;
	}

	@Override
	public void loopCall(int i) {
		anotherService.work(i);
	}

	@Override
	public void work() {
		System.out.println("ThirdService.work was call");
	}
}

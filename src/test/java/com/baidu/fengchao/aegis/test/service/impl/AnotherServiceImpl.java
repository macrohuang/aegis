package com.baidu.fengchao.aegis.test.service.impl;

import com.baidu.fengchao.aegis.test.service.AnotherService;
import com.baidu.fengchao.aegis.test.service.BusinessService;
import com.baidu.fengchao.aegis.test.service.ThirdInterface;

public class AnotherServiceImpl implements AnotherService, ThirdInterface {

	private BusinessService businessService;
	@Override
	public void work(int i) {
		if (i > 0) {
			System.out.print(i);
			work();
			businessService.loopCall(i - 1);
		}
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	@Override
	public void work() {
		System.out.print("I am work from ThirdService");
	}
}

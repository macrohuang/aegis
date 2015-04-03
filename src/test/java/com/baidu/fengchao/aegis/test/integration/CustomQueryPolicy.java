package com.baidu.fengchao.aegis.test.integration;

import java.util.Arrays;

import com.baidu.fengchao.aegis.policy.Policy;

public class CustomQueryPolicy implements Policy {

	@Override
    public Object getResult(Object[] exceedKeys, Object... params) {
		return Arrays.asList(exceedKeys);
	}
}

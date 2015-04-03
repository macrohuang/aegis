package com.baidu.fengchao.aegis.policy;

import java.util.Arrays;

import org.apache.commons.lang.Validate;

import com.baidu.fengchao.aegis.test.service.WebBatchModResponse;

public class ModWordPolicy implements Policy {
	@Override
    public Object getResult(Object[] exceedKeys, Object... params) {
        Validate.notNull(params);
		System.out.println("Exceed kyes: " + Arrays.toString(exceedKeys));
		WebBatchModResponse response = new WebBatchModResponse();
		response.setStatus(500);
		return response;
	}
}

package com.baidu.fengchao.aegis.test.integration;

import com.baidu.fengchao.aegis.judgement.Judgment;

public class CustomBeanIds implements Judgment {

	@Override
    public boolean block(Object key, Object... params) {
		System.out.println(key + " is using custom bean judgment!");
		return true;
	}
}

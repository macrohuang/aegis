package com.macrohuang.aegis.test.integration;

import com.macrohuang.aegis.policy.Policy;

public class CustomBeanPolicy implements Policy {
	public static final String s = "Oh, no, you are blocked!!";
	@Override
    public Object getResult(Object[] exceedKeys, Object... params) {
		return s;
	}
}

package com.macrohuang.aegis.test.integration;

import java.util.Arrays;

import com.macrohuang.aegis.policy.Policy;

public class CustomPolicy implements Policy {

	@Override
    public Object getResult(Object[] exceedKeys, Object... params) {
		return "XXOO:" + Arrays.toString(exceedKeys);
	}
}

package com.baidu.fengchao.aegis.policy;

import org.apache.commons.lang.Validate;

import com.baidu.fengchao.aegis.key.BlockedKey;
import com.baidu.fengchao.aegis.test.service.ModWordRequest;

public class ModWordBlockedKeys implements BlockedKey {

	@Override
    public Object[] getBlockedKeys(Object... params) {
        Validate.notNull(params);
        Validate.isTrue(params.length > 1);
        ModWordRequest request = (ModWordRequest) params[1];
		return request.getWinfoid().toArray();
	}
}

package com.macrohuang.adcore.api.word;

import org.apache.commons.lang.Validate;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.strategy.FrameLimitStrategy;

public class MyStrategy extends FrameLimitStrategy {

	public MyStrategy() {

	}
	public MyStrategy(BlockedTimeType timeType, long limit) {
		super(timeType, limit);
	}

	@Override
    public boolean isExceedLimit(BlockedPoint point, Object blockedKey, Object... params) {
        Validate.notNull(params);
        Validate.isTrue(params.length > 2);
		// WordCountRequest[] requests = (WordCountRequest)
		// joinPoint.getArgs()[2];
		// TODO:这里实现
		// 如果condition属性包含WordConditionField.unitid，则执行super.isExceedLimit(joinPoint,blockedKey)，否则返回false
        return super.isExceedLimit(point, blockedKey, params);
	}
}

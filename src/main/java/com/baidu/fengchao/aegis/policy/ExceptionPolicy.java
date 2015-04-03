package com.baidu.fengchao.aegis.policy;

import java.util.Arrays;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.exception.TooFrequentInvokeException;

/**
 * If invoke exceed limit, then a <code>TooFrequentInvokeException</code> will
 * be thrown.
 * 
 * @author Macro Huang
 * 
 */
public class ExceptionPolicy implements Policy {

    private BlockedTimeType timeType;
    private long limit;

    public ExceptionPolicy(BlockedTimeType timeType, long limit) {
        this.timeType = timeType;
        this.limit = limit;
    }

    @Override
    public Object getResult(Object[] exceedKeys, Object... params) throws TooFrequentInvokeException {
        throw new TooFrequentInvokeException(String.format("限制每%s访问%d次，%s超出访问限制！", timeType.getDescription(), limit,
                Arrays.toString(exceedKeys)), true);
    }
}

package com.baidu.fengchao.aegis.policy;

import org.junit.Test;

import com.baidu.fengchao.aegis.api.BlockedConstants.BlockedTimeType;
import com.baidu.fengchao.aegis.exception.TooFrequentInvokeException;

public class ExceptionPolicyTest extends BasePolicy {
    @Test(expected = TooFrequentInvokeException.class)
    public void testGetResult() throws TooFrequentInvokeException {
        new ExceptionPolicy(BlockedTimeType.Minute, 100).getResult(null);
    }
}

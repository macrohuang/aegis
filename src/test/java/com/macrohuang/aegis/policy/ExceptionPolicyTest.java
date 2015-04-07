package com.macrohuang.aegis.policy;

import org.junit.Test;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.exception.TooFrequentInvokeException;

public class ExceptionPolicyTest extends BasePolicy {
    @Test(expected = TooFrequentInvokeException.class)
    public void testGetResult() throws TooFrequentInvokeException {
        new ExceptionPolicy(BlockedTimeType.Minute, 100).getResult(null);
    }
}

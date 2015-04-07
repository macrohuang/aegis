package com.macrohuang.aegis.policy;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @title NullPolicyTest
 * @description TODO
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-3-19
 * @version 1.0
 */
public class NullPolicyTest extends BasePolicy {
	@Test
	public void testGetResult() {
        Assert.assertNull(new NullPolicy().getResult(null));
	}
}

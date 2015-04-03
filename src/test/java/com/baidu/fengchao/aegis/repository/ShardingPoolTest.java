/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.fengchao.aegis.repository;

import org.junit.Test;

import com.baidu.fengchao.skynet.repository.ShardingPool;

/**
 * @title ShardingPoolTest
 * @description TODO 
 * @author huangtianlai(huangtianlai01@baidu.com)
 * @date 2014-7-4
 * @version 1.0
 */
public class ShardingPoolTest {
    @Test
    public void testInit() {
        ShardingPool pool = new ShardingPool();
        pool.setAddress("dbl-fc-rd06.vm.baidu.com:6379;dbl-fc-rd06.vm.baidu.com;dbl-fc-rd06.vm.baidu.com:6379");
        pool.setMaxActive(20);
        pool.setMaxIdle(10);
        pool.setMaxWait(1000);
        pool.setPasswd("altair-taskcenter");
        pool.setTestOnBorrow(false);
        pool.setTimeout(6000);
        pool.init();
    }

    @Test(expected = RuntimeException.class)
    public void testInitFail() {
        ShardingPool pool = new ShardingPool();
        pool.init();
    }
}

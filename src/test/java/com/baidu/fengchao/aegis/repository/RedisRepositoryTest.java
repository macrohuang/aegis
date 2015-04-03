package com.baidu.fengchao.aegis.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.fengchao.skynet.repository.CounterRepository;
import com.baidu.fengchao.skynet.repository.RedisCounterRepository;

public class RedisRepositoryTest {
	private static final String key ="aegis_test_key";
	private static final String key2 ="aegis_test_key_2";
    CounterRepository counterRepository;
	@Before
	public void init(){
        ApplicationContext context = new ClassPathXmlApplicationContext("blocked-base.xml");
        counterRepository = context.getBean(RedisCounterRepository.class);
	}
	@Test
	public void testGetLastAccessTime(){
		long t = System.currentTimeMillis();
        counterRepository.updateLastAccessTime(key, t);
        Assert.assertEquals(t, counterRepository.getLastAccessTime(key));
	}

	@Test
	public void testUpdateLastAccessTime(){
		long t = System.currentTimeMillis();
        counterRepository.updateLastAccessTime(key, t);
		t = System.currentTimeMillis();
        counterRepository.updateLastAccessTime(key, t);
        Assert.assertEquals(t, counterRepository.getLastAccessTime(key));
		
	}
	@Test
	public void testGetAndAddAccessList(){
        counterRepository.getAccessList(key2);
		List<Long> list = Arrays.asList(System.nanoTime(),System.nanoTime(),System.nanoTime(),System.nanoTime(),System.nanoTime());
		for (Long l:list){
            counterRepository.addAccessRecord(key2, l);
		}
        List<Long> list2 = counterRepository.getAccessList(key2);
		System.out.println(list);
		System.out.println(list2);
		Assert.assertTrue(list2.containsAll(list));
	}
	@Test
	public void testRemoveAccessRecord(){
        counterRepository.getAccessList(key2);
		List<Long> list = Arrays.asList(System.nanoTime(),System.nanoTime(),System.nanoTime(),System.nanoTime(),System.nanoTime());
		for (Long l:list){
            counterRepository.addAccessRecord(key2, l);
		}
        List<Long> list2 = counterRepository.getAccessList(key2);
		int count = list2.size() - list.size();
        counterRepository.removeAccessRecord(key2, count);
        Assert.assertEquals(list, counterRepository.getAccessList(key2));
	}
}

package com.baidu.fengchao.aegis.api;

import org.aspectj.lang.ProceedingJoinPoint;

import com.baidu.fengchao.aegis.exception.TooFrequentInvokeException;
import com.baidu.fengchao.ether.api.IConfCenterClient;
import com.baidu.fengchao.ether.impl.ConfCenterClientFactory;
import com.baidu.fengchao.skynet.service.impl.SkynetServiceImpl;

public class BlockedServiceAspect extends SkynetServiceImpl {

    public BlockedServiceAspect() {

    }
    public BlockedServiceAspect(String confCenterProperties) {
        setConfCenterClient(ConfCenterClientFactory.getConfCenterClient(confCenterProperties));
    }

    public BlockedServiceAspect(IConfCenterClient confCenter) {
        setConfCenterClient(confCenter);
    }

    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            String blockedPointID = joinPoint.getTarget().getClass().getName() + "."
                    + joinPoint.getSignature().getName();
            check(blockedPointID, joinPoint.getArgs());
        } catch (TooFrequentInvokeException e) {
            if (e.isResult()) {
                throw e;
            } else {
                return e.getResult();
            }
        }
        return joinPoint.proceed();
    }
}

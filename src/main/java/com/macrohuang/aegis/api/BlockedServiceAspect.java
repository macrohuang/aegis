package com.macrohuang.aegis.api;

import org.aspectj.lang.ProceedingJoinPoint;

import com.macrohuang.aegis.exception.TooFrequentInvokeException;
import com.macrohuang.aegis.service.impl.AegisServiceImpl;

public class BlockedServiceAspect extends AegisServiceImpl {

    public BlockedServiceAspect() {

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

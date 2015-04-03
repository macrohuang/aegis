package com.baidu.fengchao.aegis.policy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

public class BasePolicy {
	protected ProceedingJoinPoint getProceedingJoinPoint() {
		return new ProceedingJoinPoint() {

			@Override
			public String toShortString() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toLongString() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getThis() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getTarget() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StaticPart getStaticPart() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SourceLocation getSourceLocation() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Signature getSignature() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getKind() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] getArgs() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set$AroundClosure(AroundClosure arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object proceed(Object[] arg0) throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object proceed() throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}

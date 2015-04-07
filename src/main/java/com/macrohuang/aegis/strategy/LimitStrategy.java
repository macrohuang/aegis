package com.macrohuang.aegis.strategy;

import com.macrohuang.aegis.api.BlockedConstants.BlockedTimeType;
import com.macrohuang.aegis.bo.BlockedPoint;
import com.macrohuang.aegis.repository.CounterRepository;

/**
 * 
 * @title LimitStrategy
 * @description 封禁策略的接口
 * @author Macro Huang (macrohuang.whu@gmail.com)
 * @date 2014-3-27
 * @version 1.0
 */
public interface LimitStrategy {
    /**
     * Weather this invoke is exceed the limite.
     * 
     * @param blockedKey
     *            The limit key.
     * @return <code>true</code> if the invoke is exceed invoke strategy,
     *         <code>false</code> for otherwise.
     */
    public boolean isExceedLimit(BlockedPoint point, Object blockedKey, Object... params);

    /**
     * 封禁的时间类型，支持 秒、分、小时、天 四种粒度的时间
     * 
     * @param timeType
     *            时间类型
     */
    public void setTimeType(BlockedTimeType timeType);

    /**
     * 设置封禁阈值
     * 
     * @param limit
     *            封禁阈值
     */
    public void setLimit(long limit);

    /**
     * 设置被封禁后要阻止多长时间的访问，单位为S
     * 
     * @param block
     *            要阻止访问的时间，单位为S
     */
    public void setBlock(long block);

    /**
     * 设置计算周期，默认是1，表示1分钟/1秒/1小时/1天限制limit次， 
     * 该值大于1时表示duration分钟/秒/小时/天限制limit次
     * 
     * @param duration
     *            周期，自然数
     */
    public void setDuration(int duration);

    public void setCounterRepository(CounterRepository repository);
}

package com.starblues.rope.core.transport;

import com.codahale.metrics.MetricSet;
import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.model.RecordWrapper;

/**
 * 数据运输器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Transport extends Identity {

    /**
     * 启动运输器
     * @throws Exception 启动异常
     */
    void start() throws Exception;

    /**
     * 停止运输器
     * @throws Exception 停止异常
     */
    void stop() throws Exception;

    /**
     * 当前运输器的状态
     * @return 运输器的状态
     */
    State state();


    /**
     * 输入传输数据
     * @param recordWrapper 记录包装者
     */
    void input(RecordWrapper recordWrapper);

    /**
     * 得到指标监控 MetricSet
     * @return MetricSet
     */
    MetricSet getMetricSet();

}

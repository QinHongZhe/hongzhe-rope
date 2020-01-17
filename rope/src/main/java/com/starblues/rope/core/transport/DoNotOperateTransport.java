package com.starblues.rope.core.transport;

import com.codahale.metrics.MetricSet;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.model.RecordWrapper;

/**
 * 不可操作的Transport
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateTransport implements Transport{

    private final Transport transport;

    public DoNotOperateTransport(Transport transport) {
        this.transport = transport;
    }


    @Override
    public void start() throws Exception {
        throw new Exception("Do not operate start");
    }

    @Override
    public void stop() throws Exception {
        throw new Exception("Do not operate stop");
    }

    @Override
    public State state() {
        return transport.state();
    }

    @Override
    public void input(RecordWrapper recordWrapper) {
        transport.input(recordWrapper);
    }

    @Override
    public MetricSet getMetricSet() {
        return transport.getMetricSet();
    }

    @Override
    public String id() {
        return transport.id();
    }

    @Override
    public String name() {
        return transport.name();
    }

    @Override
    public String describe() {
        return transport.describe();
    }
}

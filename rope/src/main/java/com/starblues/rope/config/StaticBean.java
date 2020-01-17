package com.starblues.rope.config;

import com.codahale.metrics.MetricRegistry;

/**
 * 静态bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class StaticBean {

    private StaticBean(){

    }

    public static final MetricRegistry metricRegistry = new MetricRegistry();





}

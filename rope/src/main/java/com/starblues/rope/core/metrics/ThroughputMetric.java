package com.starblues.rope.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 基本的吞吐量
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ThroughputMetric implements MetricSet {


    /**
     * 字节数。用于统计字节数
     */
    public static final String NAMESPACE = "throughput";

    private final static String COUNT_NAME = "count";
    private final static String BYTE_NAME = "byte";

    private final String group;


    private final Counter countCounter;
    private final Counter byteCounter;


    public ThroughputMetric(String group, String ...groups) {
        if(StringUtils.isEmpty(group)){
            throw new IllegalArgumentException("Group can't is empty");
        }
        this.group = MetricRegistry.name(group, groups);
        this.countCounter = new Counter();
        this.byteCounter = new Counter();
    }


    @Override
    public Map<String, Metric> getMetrics() {
        Map<String, Metric> metricMap = new HashMap<>();
        metricMap.put(countCounterName(), countCounter);
        metricMap.put(byteCounterName(), byteCounter);
        return metricMap;
    }


    public Counter countCounter() {
        return countCounter;
    }

    public Counter byteCounter() {
        return byteCounter;
    }

    public String countCounterName(){
        return MetricRegistry.name(NAMESPACE, group, COUNT_NAME);
    }

    public String byteCounterName(){
        return MetricRegistry.name(NAMESPACE, group, BYTE_NAME);
    }



}

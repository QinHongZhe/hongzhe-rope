package com.starblues.rope.core.transport;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的MetricSet
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultMetricSet implements MetricSet {

    private final Map<String, Metric> metricMap = new HashMap<>();


    public void addMetric(String name, Metric metric){
        metricMap.put(name, metric);
    }



    @Override
    public Map<String, Metric> getMetrics() {
        return metricMap;
    }
}

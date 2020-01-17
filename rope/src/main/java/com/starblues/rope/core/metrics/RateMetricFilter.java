package com.starblues.rope.core.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.starblues.rope.config.constant.MetricsConstant;

/**
 * 计算后的速率匹配器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class RateMetricFilter implements MetricFilter {

    private final String group;

    public RateMetricFilter(String group, String ...names) {
        if(names == null){
            this.group = group;
        } else {
            this.group = MetricRegistry.name(group, names);
        }
    }


    @Override
    public boolean matches(String name, Metric metric) {
        return name.startsWith(MetricRegistry.name(ThroughputMetric.NAMESPACE, group))
                && name.endsWith(MetricsConstant.RATE_SUFFIX);
    }

}

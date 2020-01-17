package com.starblues.rope.service.metrics;

import com.codahale.metrics.Gauge;
import com.starblues.rope.config.StaticBean;
import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.metrics.RateMetricFilter;
import com.starblues.rope.utils.MetricUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * 吞吐量服务实现类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class ThroughputServiceImpl implements ThroughputService{

    @Override
    public List<Map<String, Object>> getInputTransport() {
        RateMetricFilter filter = new RateMetricFilter(MetricsConstant.GROUP_INPUT_TRANSPORT);
        SortedMap<String, Gauge> gauges = StaticBean.metricRegistry.getGauges(filter);
        return MetricUtils.mapAll(gauges);
    }

    @Override
    public List<Map<String, Object>> getReader(String processId) {
        RateMetricFilter filter = new RateMetricFilter(MetricsConstant.GROUP_INPUT_READER, processId);
        SortedMap<String, Gauge> gauges = StaticBean.metricRegistry.getGauges(filter);
        return MetricUtils.mapAll(gauges);
    }

    @Override
    public List<Map<String, Object>> getOutputTransport() {
        RateMetricFilter filter = new RateMetricFilter(MetricsConstant.GROUP_OUTPUT_TRANSPORT);
        SortedMap<String, Gauge> gauges = StaticBean.metricRegistry.getGauges(filter);
        return MetricUtils.mapAll(gauges);
    }

    @Override
    public List<Map<String, Object>> getWriters(String processId) {
        RateMetricFilter filter = new RateMetricFilter(MetricsConstant.GROUP_OUTPUT_WRITER, processId);
        SortedMap<String, Gauge> gauges = StaticBean.metricRegistry.getGauges(filter);
        return MetricUtils.mapAll(gauges);
    }

    @Override
    public List<Map<String, Object>> getWriters() {
        RateMetricFilter filter = new RateMetricFilter(MetricsConstant.GROUP_OUTPUT_WRITER);
        SortedMap<String, Gauge> gauges = StaticBean.metricRegistry.getGauges(filter);
        return MetricUtils.mapAll(gauges);
    }
}

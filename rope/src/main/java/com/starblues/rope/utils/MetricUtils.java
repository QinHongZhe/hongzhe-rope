package com.starblues.rope.utils;

import com.codahale.metrics.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Metric 指标工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class MetricUtils {

    private MetricUtils(){}

    public static <T extends Metric> List<Map<String, Object>> mapAll(Map<String, T> metrics) {
        return mapAllFiltered(metrics, null);
    }

    public static <T extends Metric> List<Map<String, Object>> mapAllFiltered(Map<String, T> metrics,
                                                                        Set<String> blacklist) {
        List<Map<String, Object>> result = Lists.newArrayList();

        if (metrics == null) {
            return result;
        }

        for (Map.Entry<String, T> metric : metrics.entrySet()) {
            boolean filtered = false;
            if (blacklist != null) {
                for(String x : blacklist) {
                    if (metric.getKey().startsWith(x)) {
                        filtered = true;
                        break;
                    }
                }
            }

            if (filtered) {
                continue;
            }

            result.add(map(metric.getKey(), metric.getValue()));
        }

        return result;
    }

    public static Map<String, Object> map(String metricName, Metric metric) {
        String type = metric.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);

        if (type.isEmpty()) {
            type = "gauge";
        }

        Map<String, Object> metricMap = Maps.newHashMap();
        metricMap.put("full_name", metricName);
        List<String> stringList = Splitter.on(".")
                .trimResults()
                .splitToList(metricName);
        if(stringList.size() >= 2){
            metricMap.put("name", stringList.get(stringList.size() - 2) + "." +
                    stringList.get(stringList.size() - 1));
        } else {
            metricMap.put("name", metricName);
        }
        metricMap.put("type", type);

        if (metric instanceof Timer) {
            metricMap.put("metric", buildTimerMap((Timer) metric));
        } else if(metric instanceof Meter) {
            metricMap.put("metric", buildMeterMap((Meter) metric));
        } else if(metric instanceof Histogram) {
            metricMap.put("metric", buildHistogramMap((Histogram) metric));
        } else {
            metricMap.put("metric", metric);
        }
        return metricMap;
    }

    public static TimerRateMetricsResponse buildTimerMap(Timer t) {
        final TimerRateMetricsResponse result = new TimerRateMetricsResponse();
        final TimerMetricsResponse time = new TimerMetricsResponse();
        final RateMetricsResponse rate = new RateMetricsResponse();

        if (t == null) {
            return result;
        }

        time.max = TimeUnit.MICROSECONDS.convert(t.getSnapshot().getMax(), TimeUnit.NANOSECONDS);
        time.min = TimeUnit.MICROSECONDS.convert(t.getSnapshot().getMin(), TimeUnit.NANOSECONDS);
        time.mean = TimeUnit.MICROSECONDS.convert((long) t.getSnapshot().getMean(), TimeUnit.NANOSECONDS);
        time.percentile95th = TimeUnit.MICROSECONDS.convert((long) t.getSnapshot().get95thPercentile(), TimeUnit.NANOSECONDS);
        time.percentile98th = TimeUnit.MICROSECONDS.convert((long) t.getSnapshot().get98thPercentile(), TimeUnit.NANOSECONDS);
        time.percentile99th = TimeUnit.MICROSECONDS.convert((long) t.getSnapshot().get99thPercentile(), TimeUnit.NANOSECONDS);
        time.stdDev = TimeUnit.MICROSECONDS.convert((long) t.getSnapshot().getStdDev(), TimeUnit.NANOSECONDS);

        rate.oneMinute = t.getOneMinuteRate();
        rate.fiveMinute = t.getFiveMinuteRate();
        rate.fifteenMinute = t.getFifteenMinuteRate();
        rate.total = t.getCount();
        rate.mean = t.getMeanRate();

        result.time = time;
        result.rate = rate;
        result.rateUnit = "events/second";
        result.durationUnit = TimeUnit.MICROSECONDS.toString().toLowerCase(Locale.ENGLISH);

        return result;
    }

    public static Map<String, Object> buildHistogramMap(Histogram h) {
        Map<String, Object> metrics = Maps.newHashMap();

        if (h == null) {
            return metrics;
        }

        Map<String, Object> time = Maps.newHashMap();
        time.put("max", h.getSnapshot().getMax());
        time.put("min", h.getSnapshot().getMin());
        time.put("mean", (long) h.getSnapshot().getMean());
        time.put("95th_percentile", (long) h.getSnapshot().get95thPercentile());
        time.put("98th_percentile", (long) h.getSnapshot().get98thPercentile());
        time.put("99th_percentile", (long) h.getSnapshot().get99thPercentile());
        time.put("std_dev", (long) h.getSnapshot().getStdDev());

        metrics.put("time", time);
        metrics.put("count", h.getCount());

        return metrics;
    }

    public static Map<String, Object> buildMeterMap(Meter m) {
        Map<String, Object> metrics = Maps.newHashMap();

        if (m == null) {
            return metrics;
        }

        Map<String, Object> rate = Maps.newHashMap();
        rate.put("one_minute", m.getOneMinuteRate());
        rate.put("five_minute", m.getFiveMinuteRate());
        rate.put("fifteen_minute", m.getFifteenMinuteRate());
        rate.put("total", m.getCount());
        rate.put("mean", m.getMeanRate());

        metrics.put("rate_unit", "events/second");
        metrics.put("rate", rate);

        return metrics;
    }

    public static MetricFilter filterSingleMetric(String name) {
        return new SingleMetricFilter(name);
    }

    public static void safelyRegister(MetricRegistry metricRegistry, MetricSet metricSet) {
        if(metricSet == null){
            return;
        }
        Map<String, Metric> metrics = metricSet.getMetrics();
        if(metrics == null || metrics.isEmpty()){
            return;
        }
        metrics.forEach((name, metric)->{
            safelyRegister(metricRegistry, name, metric);
        });
    }


    public static <T extends Metric> T safelyRegister(MetricRegistry metricRegistry, String name, T metric) {
        try {
            return metricRegistry.register(name, metric);
        } catch (IllegalArgumentException ignored) {
            // safely ignore already existing metric, and simply return the one registered previously.
            // note that we do not guard against differing metric types here, we consider that a programming error for now.

            //noinspection unchecked
            return (T) metricRegistry.getMetrics().get(name);
        }
    }

    public static void safelyRegisterAll(MetricRegistry metricRegistry, MetricSet metrics) throws IllegalArgumentException {
        try {
            metricRegistry.registerAll(metrics);
        } catch (IllegalArgumentException e) {
            log.error("Duplicate metric set registered", e);
        }
    }

    public static Gauge<Long> constantGauge(final long constant) {
        return new Gauge<Long>() {
            @Override
            public Long getValue() {
                return constant;
            }
        };
    }

    public static class SingleMetricFilter implements MetricFilter {
        private final String allowedName;
        public SingleMetricFilter(String allowedName) {
            this.allowedName = allowedName;
        }

        @Override
        public boolean matches(String name, Metric metric) {
            return allowedName.equals(name);
        }
    }




    @Data
    public static class TimerRateMetricsResponse{
        public TimerMetricsResponse time;
        public RateMetricsResponse rate;
        public String durationUnit;
        public String rateUnit;
    }

    @Data
    public static class TimerMetricsResponse{
        public double min;
        public double max;
        public double mean;
        private double stdDev;
        private double percentile95th;
        private double percentile98th;
        private double percentile99th;
    }

    @Data
    public static class RateMetricsResponse{
        private double total;
        private double mean;
        private double oneMinute;
        private double fiveMinute;
        private double fifteenMinute;
    }

}

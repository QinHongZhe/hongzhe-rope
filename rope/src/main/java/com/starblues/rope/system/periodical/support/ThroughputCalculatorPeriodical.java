package com.starblues.rope.system.periodical.support;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Maps;
import com.starblues.rope.config.StaticBean;
import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.metrics.ThroughputMetric;
import com.starblues.rope.system.periodical.AbstractPeriodical;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 吞吐量计算器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class ThroughputCalculatorPeriodical extends AbstractPeriodical {

    private ConcurrentMap<String, CounterSample> sampledCounters = Maps.newConcurrentMap();
    private final MetricFilter metricFilter = MetricFilter.startsWith(ThroughputMetric.NAMESPACE);


    @Override
    public boolean runsForever() {
        return true;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return false;
    }

    @Override
    public int getDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 1;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected void doRun() {
        SortedMap<String, Counter> counters = StaticBean.metricRegistry.getCounters(metricFilter);
        counters.forEach((metricName, counter)->{
            CounterSample counterSample = sampledCounters.get(metricName);
            if (counterSample == null) {
                counterSample = new CounterSample();
                sampledCounters.put(metricName, counterSample);
            }
            counterSample.updateAverage(counter.getCount());
            String rateName = MetricRegistry.name(metricName, MetricsConstant.RATE_SUFFIX);
            if (!StaticBean.metricRegistry.getMetrics().containsKey(rateName)) {
                try {
                    log.debug("Registering derived, per-second metric {}", rateName);
                    StaticBean.metricRegistry.register(rateName, new Gauge<Double>() {
                        @Override
                        public Double getValue() {
                            final CounterSample sample = sampledCounters.get(metricName);
                            return sample == null ? 0d : sample.getCurrentAverage();
                        }
                    });
                } catch (IllegalArgumentException e) {
                    log.warn(
                            "Could not register gauge {} despite checking before that it didn't exist. This should not happen.",
                            rateName);
                }
            }
        });
    }

    private static class CounterSample {
        private long previousCount = 0L;
        private double currentAverage = 0d;


        void updateAverage(long currentCount) {
            currentAverage = currentCount - previousCount;
            previousCount = currentCount;
        }

        public double getCurrentAverage() {
            return currentAverage;
        }
    }

}

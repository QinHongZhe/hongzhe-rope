package com.starblues.rope.core.transport;

import com.codahale.metrics.MetricSet;
import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.metrics.ThroughputMetric;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.utils.TextUtils;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

/**
 * 抽象的Transport.主要控制状态
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractTransport implements Transport{

    private final StateControl stateControl = new StateControl();
    private final ThroughputMetric throughputMetric;


    protected AbstractTransport() {
        String simpleName = this.getClass().getSimpleName();
        String className =  TextUtils.toLowerCaseFirstOne(simpleName);

        String metricGroup = metricGroup();
        if(StringUtils.isEmpty(metricGroup)){
            metricGroup = MetricsConstant.GROUP_TRANSPORT;
        }

        throughputMetric = new ThroughputMetric(metricGroup, className);
    }

    @Override
    public void start() throws Exception {
        stateControl.start();
        try {
            stateControl.startSuccessful();
            startAfter();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        stateControl.stop();
        try {
            stopAfter();
            stateControl.stopSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }

    /**
     * 父类启动执行后的操作
     * @throws Exception 启动抛出的异常
     */
    protected abstract void startAfter() throws Exception;

    /**
     * 父类停止执行后的操作
     * @throws Exception 停止抛出的异常
     */
    protected abstract void stopAfter() throws Exception;

    /**
     * 子类可新增Metric
     * @param metricSet DefaultMetricSet
     */
    protected void addMetric(DefaultMetricSet metricSet){

    }

    /**
     * 子类调用记录指标
     * @param recordWrapper 数据的包装对象
     */
    protected void metric(RecordWrapper recordWrapper){
        try {
            RecordGroup recordGroup = recordWrapper.getRecordGroup();
            if(recordGroup == null){
                return;
            }
            throughputMetric.countCounter().inc(recordGroup.size());
            throughputMetric.byteCounter().inc(recordGroup.getByteSize());
        } catch (Exception e){
            logger().debug("Metric error. {}", e.getMessage(), e);
        }
    }


    @Override
    public MetricSet getMetricSet() {
        return throughputMetric;
    }

    @Override
    public State state() {
        return stateControl.getCurrentState();
    }

    /**
     * 指标分类
     * @return 指标分类
     */
    protected abstract String metricGroup();

    /**
     * 子类返回日志输出者
     * @return Logger
     */
    protected abstract Logger logger();

}

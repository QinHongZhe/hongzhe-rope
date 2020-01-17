package com.starblues.rope.core.input.support.reader;

import com.google.common.collect.ImmutableMap;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.input.AbstractReaderInput;
import com.starblues.rope.utils.ExceptionMsgUtils;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 周期型获取数据的输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PeriodAcquireReaderInput extends AbstractReaderInput implements Runnable{

    public static final String ID = "period-acquire";

    private final Param param;
    private ScheduledFuture<?> scheduledFuture;

    public PeriodAcquireReaderInput() {
        this.param = new Param();
    }


    @Override
    public void initialize() throws Exception {
    }

    @Override
    public ConfigParameter configParameter() {
        return param;
    }

    @Override
    public void run() {
        super.readRecord();
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "周期输入(java)";
    }

    @Override
    public String describe() {
        return "周期性获取数据的输入";
    }


    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @Getter
    public static class Param implements ConfigParameter {


        public static final String PERIOD = "period";
        public static final String DELAY = "delay";
        public static final String TIME_UNIT = "timeUnit";

        private static final Map<String, String> TIME_UNIT_OPTIONS = ImmutableMap.of(
                TimeUnit.SECONDS.toString(), "秒",
                TimeUnit.MINUTES.toString(), "分钟",
                TimeUnit.HOURS.toString(), "小时",
                TimeUnit.DAYS.toString(), "天"
        );

        private Integer period;
        private Integer delay = 0;
        private TimeUnit timeUnit;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            period = configParamInfo.getInt(PERIOD);
            if(period == null){
                throw ExceptionMsgUtils.getInputParamException(ID, PERIOD);
            }
            String timeUnitString = configParamInfo.getString(TIME_UNIT);
            if (StringUtils.isEmpty(timeUnitString)) {
                throw ExceptionMsgUtils.getInputParamException(ID, TIME_UNIT);
            }
            if(delay != null){
                delay = configParamInfo.getInt(DELAY);
            }
            timeUnit = Enum.valueOf(TimeUnit.class, timeUnitString);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();
            configParam.addField(
                    NumberField.toBuilder(PERIOD, "周期值", 10)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("执行周期值")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(DELAY, "延迟值", 0)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("延迟执行值")
                            .build()
            );


            configParam.addField(
                    DropdownField.toBuilder(TIME_UNIT, "单位",  TimeUnit.SECONDS.toString(),
                            TIME_UNIT_OPTIONS)
                            .required(true)
                            .description("周期值、延迟值的单位")
                            .build()
            );


            return configParam;
        }
    }

}

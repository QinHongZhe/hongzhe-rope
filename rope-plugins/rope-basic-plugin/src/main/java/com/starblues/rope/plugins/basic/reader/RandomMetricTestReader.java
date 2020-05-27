package com.starblues.rope.plugins.basic.reader;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.reader.BaseReaderConfigParameter;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 随机性能测试读取者
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-14
 */
@Component
public class RandomMetricTestReader implements Reader {

    private final static String ID = "random-metric-test";

    private Param param;

    /**
     * 是否运行
     */
    private AtomicBoolean isRun = new AtomicBoolean(false);

    public RandomMetricTestReader(){
        this.param = new Param();
    }


    @Override
    public void initialize(String processId) throws Exception {
        isRun.set(true);
    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        for (int i = 0; i < param.getLoopNumber(); i++) {
            if(!isRun.get()){
                // 没有运行直接返回
                return;
            }
            RecordGroup recordGroup = new RecordGroup();
            for (int j = 0; j < param.getRowCount(); j++) {
                Record record = DefaultRecord.instance();
                record.putColumn(Column.auto(param.getKey(), param.getValue()));
                recordGroup.addRecord(record);
            }
            if(!isRun.get()){
                // 没有运行直接返回
                return;
            }
            consumer.accept(recordGroup);
            Thread.sleep(param.getDelayedMs());
        }
    }

    @Override
    public void destroy() throws Exception {
        isRun.set(false);
    }

    @Override
    public BaseReaderConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "随机性能测试";
    }

    @Override
    public String describe() {
        return "随机性能测试读取者";
    }

    @Getter
    public static class Param extends BaseReaderConfigParameter {

        private static final String KEY = "key";
        private static final String VALUE = "value";
        private static final String LOOP_NUMBER = "loopNumber";
        private static final String ROW_COUNT = "rowCount";
        private static final String DELAYED_MS = "delayedMs";


        /**
         * 记录字段的key
         */
        private String key;


        /**
         * 产生的值
         */
        private String value;

        /**
         * 循环次数
         */
        private int loopNumber = 100;

        /**
         * 一次产生条数
         */
        private int rowCount = 100;

        /**
         * 延时。单位毫秒
         */
        private int delayedMs = 1000;




        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            key = paramInfo.getString(KEY, "random_key");
            value = paramInfo.getString(VALUE, "random_value");
            loopNumber = paramInfo.getInt(LOOP_NUMBER, loopNumber);
            rowCount = paramInfo.getInt(ROW_COUNT, rowCount);
            delayedMs = paramInfo.getInt(DELAYED_MS, delayedMs);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            configParam.addField(
                    TextField.toBuilder(
                            KEY, "字段key", "random_key")
                            .description("记录字段的key")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            VALUE, "字段值", "random_value")
                            .description("字段key对应的值")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            LOOP_NUMBER, "循环次数", 100)
                            .description("执行一次的循环次数")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(true)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            ROW_COUNT, "产生条数", 100)
                            .description("循环一次产生的条数")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(true)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            DELAYED_MS, "延迟", 1000)
                            .description("循环间隔延迟数, 单位毫秒")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(true)
                            .build()
            );
        }
    }


}

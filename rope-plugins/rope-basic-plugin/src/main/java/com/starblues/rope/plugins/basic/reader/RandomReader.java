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
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 随机输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class RandomReader implements Reader{

    private final static String ID = "random";

    private Random rand = new Random();

    private final Param param;


    public RandomReader(){
        this.param = new Param();
    }


    @Override
    public void initialize(String processId) throws Exception {

    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        Record record = DefaultRecord.instance();
        Integer i = rand.nextInt(param.getMaxNum());
        record.putColumn(Column.auto(param.getKey(), i));
        consumer.accept(record);
    }


    @Override
    public void destroy() throws Exception {

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
        return "随机输入";
    }

    @Override
    public String describe() {
        return "随机输入";
    }

    @Getter
    public static class Param extends BaseReaderConfigParameter {

        private static final String KEY = "key";
        private static final String MAX_NUM = "maxNum";

        private String key;
        private Integer maxNum;


        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            this.key = configParamInfo.getString(KEY);
            this.maxNum = configParamInfo.getInt(MAX_NUM);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            configParam.addField(
                    TextField.toBuilder(
                            KEY, "key", "random")
                            .description("随机数的key字段值")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            MAX_NUM, "最大数", 100)
                            .description("随机数最大值")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(true)
                            .build()
            );

        }
    }

}

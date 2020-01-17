package com.starblues.rope.core.output.support;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.output.AbstractCacheOutput;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * 批量输出
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class BatchOutput extends AbstractCacheOutput {

    public static final String ID = "batch";

    private final Param param;


    public BatchOutput() {
        this.param = new Param();
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "批量输出";
    }

    @Override
    public String describe() {
        return "当数据记录量达到设定数值时, 会批量输出数据记录";
    }


    @Override
    protected void outputEvent() {
        if(outputCount() >= param.getBatchSize()){
            outputToWrite();
        }
    }


    @Override
    protected void toStop() throws Exception {
        if(outputCount() > 0){
            // 说明存在数据, 则必须将数据输出
            outputToWrite();
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }



    @Override
    public void initialize() throws Exception {
    }

    @Override
    public Param configParameter() {
        return param;
    }


    @Getter
    public static class Param implements ConfigParameter {

        public final static String BATCH_SIZE = "batchSize";
        private final static Integer DEFAULT_BATCH_SIZE = 100;

        private Integer batchSize = DEFAULT_BATCH_SIZE;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            Integer batchSize = configParamInfo.getInt(BATCH_SIZE, DEFAULT_BATCH_SIZE);
            if(batchSize != null && batchSize > 0){
                this.batchSize = batchSize;
            }
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();
            configParam.addField(
                    NumberField.toBuilder(BATCH_SIZE, "批量大小", DEFAULT_BATCH_SIZE)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("当达到设定大小, 则进行批量输出")
                            .build()
            );
            return configParam;
        }
    }


}

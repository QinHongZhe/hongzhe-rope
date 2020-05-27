package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.ListMapField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自定义设置固定字段
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-19
 */
@Component
public class SetFixedFieldHandler implements DateHandler {

    private final static String ID = "set-fixed-field";

    private final Param param = new Param();

    @Override
    public void initialize(String processId) throws Exception {

    }

    @Override
    public Record handle(Record record) throws Exception {
        Map<String, String> fields = param.getFields();
        if(fields == null || fields.isEmpty()){
            return record;
        }
        fields.forEach((key, value)->{
            record.putColumn(Column.auto(key, value));
        });
        return record;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public ConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "设置固定字段";
    }

    @Override
    public String describe() {
        return "可以设置多个固定的自定义字段值";
    }

    @Getter
    public static class Param implements ConfigParameter{

        private static final String P_FIELDS = "fields";

        private static final String P_FIELD_KEY = "fieldKey";
        private static final String P_FIELD_VALUE = "fieldValue";


        /**
         * 设置的固定字段
         */
        private Map<String, String> fields = null;

        @Override
        public void parsing(ConfigParamInfo configParamInfo) {
            fields = configParamInfo.mapping(P_FIELDS, P_FIELD_KEY, P_FIELD_VALUE);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    ListMapField.toBuilder(P_FIELDS, "字段设置", P_FIELD_KEY, P_FIELD_VALUE)
                            .description("自定义设置多个固定字段")
                            .keyValueDescription("字段的key", "字段的值")
                            .required(false)
                            .build()
            );

            return configParam;
        }
    }

}

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
 * 重命名处理者
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
@Component
public class RenameFieldsHandler implements DateHandler {

    private static final String ID = "rename-fields";

    private Param param = new Param();

    @Override
    public boolean initialize(String processId) throws Exception {
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Map<String, String> fields = param.getFields();
        if(fields == null || fields.isEmpty()){
            return record;
        }

        fields.forEach((key, renameKey) -> {
            Column column = record.getColumn(key);
            if(column == null){
                return;
            }
            record.putColumn(Column.auto(renameKey, column));
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
        return "重命名";
    }

    @Override
    public String describe() {
        return "重命名字段key";
    }


    @Getter
    public static class Param implements ConfigParameter{

        private static final String P_FIELDS = "fields";

        private static final String P_FIELD_KEY = "fieldKey";
        private static final String P_RENAME_FIELD_KEY = "renameFieldKey";


        /**
         * 设置的固定字段
         */
        private Map<String, String> fields = null;

        @Override
        public void parsing(ConfigParamInfo configParamInfo) {
            fields = configParamInfo.mapping(P_FIELDS, P_FIELD_KEY, P_RENAME_FIELD_KEY);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    ListMapField.toBuilder(P_FIELDS, "字段设置", P_FIELD_KEY, P_RENAME_FIELD_KEY)
                            .description("自定义设置多个固定字段")
                            .keyValueDescription("字段的key", "重命名字段的key")
                            .required(false)
                            .build()
            );

            return configParam;
        }
    }


}

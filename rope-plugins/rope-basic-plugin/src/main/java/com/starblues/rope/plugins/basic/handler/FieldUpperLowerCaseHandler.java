package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 字段大小写转换处理者
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
@Component
public class FieldUpperLowerCaseHandler implements DateHandler {

    private final static String ID = "upper-lower-case";

    private Param param = new Param();

    @Override
    public boolean initialize(String processId) throws Exception {
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Set<String> fields = param.getFields();
        if(fields == null || fields.isEmpty()){
            fields = record.getColumnKeys();
        }
        if(fields == null || fields.isEmpty()){
            return record;
        }
        for (String columnKey : fields) {
            transform(record, columnKey);
        }
        return record;
    }


    private void transform(Record record, String columnKey){
        if(StringUtils.isEmpty(columnKey)){
            return;
        }
        Column column = record.getColumn(columnKey);
        if(column == null){
            return;
        }
        if(param.isToUpperCase()){
            // 转换大写
            column.setKey(columnKey.toUpperCase());
        } else {
            column.setKey(columnKey.toLowerCase());
        }
        record.putColumn(column);
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
        return "字段大小写转换";
    }

    @Override
    public String describe() {
        return "将字段转换成大写或者小写";
    }

    @Getter
    private static class Param implements ConfigParameter{

        private static final String P_TO_UPPER_CASE = "toUpperCase";
        private static final String P_FIELDS = "fields";

        /**
         * 是否转换成大型
         */
        private boolean toUpperCase = true;

        /**
         * 要转换的字段。如果不填, 默认转换全部字段, 多个字段使用逗号分隔
         */
        private Set<String> fields;

        @Override
        public void parsing(ConfigParamInfo paramInfo) throws Exception {
            toUpperCase = paramInfo.getBoolean(P_TO_UPPER_CASE, toUpperCase);
            fields = paramInfo.getSets(P_FIELDS, ",");
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    BooleanField.toBuilder(
                            P_TO_UPPER_CASE, "转换大写", toUpperCase)
                            .description("是否将字段转换成大写")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_FIELDS, "要转换的字段", "")
                            .description("多个字段使用逗号分隔。不填的话默认转换全部字段")
                            .required(false)
                            .build()
            );


            return configParam;
        }
    }

}

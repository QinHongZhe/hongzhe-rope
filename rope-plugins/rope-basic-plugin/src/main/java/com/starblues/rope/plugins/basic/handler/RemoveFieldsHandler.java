package com.starblues.rope.plugins.basic.handler;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Record;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 移除字段的处理者
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
@Component
public class RemoveFieldsHandler implements DateHandler {

    private static final String ID = "remove-fields";

    private Param param = new Param();

    @Override
    public boolean initialize(String processId) throws Exception {
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Set<String> removeFieldKeySet = param.getRemoveFieldKeySet();
        if(removeFieldKeySet == null || removeFieldKeySet.isEmpty()){
            return record;
        }

        for (String removeFieldKey : removeFieldKeySet) {
            record.removeColumn(removeFieldKey);
        }

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

        private static final String P_REMOVE_FIELD_KEYS = "removeFieldKeys";


        /**
         * 移除的字段key
         */
        private String removeFieldKeys;

        private Set<String> removeFieldKeySet;

        @Override
        public void parsing(ConfigParamInfo configParamInfo) {
            removeFieldKeys = configParamInfo.getString(P_REMOVE_FIELD_KEYS);
            if(!StringUtils.isEmpty(removeFieldKeys)){
                removeFieldKeySet = Sets.newHashSet(
                        Splitter.on(",")
                                .omitEmptyStrings()
                                .trimResults()
                                .split(removeFieldKeys)
                );
            }
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    TextField.toBuilder(
                            P_REMOVE_FIELD_KEYS, "字段keys", "")
                            .description("要移除的字段key, 多个使用逗号分隔")
                            .required(true)
                            .build()
            );

            return configParam;
        }
    }


}

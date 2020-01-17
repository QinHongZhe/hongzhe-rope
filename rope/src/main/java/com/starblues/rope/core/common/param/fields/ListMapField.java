package com.starblues.rope.core.common.param.fields;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Map类型的字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ListMapField extends AbstractConfigField{

    public static final String FIELD_TYPE = "listMap";


    private ListMapField(Builder builder) {
        super(builder);
    }

    /**
     * 获取Builder
     * @param key 字段的key
     * @param humanName 标题
     * @param keyProp 在map中对key的映射
     * @param valueProp Map value 的key
     * @return
     */
    public static Builder toBuilder(String key, String humanName, String keyProp, String valueProp){
        Builder builder = new Builder(FIELD_TYPE, key, humanName);
        Map<String, String> props = Maps.newHashMap();
        props.put("keyProp", keyProp);
        props.put("valueProp", valueProp);
        builder.additionalInfo.put("props", props);
        return builder;
    }


    public static class Builder extends AbstractConfigField.Builder{

        public Builder(String fieldType, String name, String humanName) {
            super(fieldType, name, humanName);
        }

        @Override
        public Builder description(String description) {
            super.description(description);
            return this;
        }

        @Override
        public Builder required(Boolean required) {
            super.required(required);
            return this;
        }

        @Override
        public Builder defaultValue(Object defaultValue) {
            return null;
        }

        @Override
        public ListMapField build() {
            return new ListMapField(this);
        }
    }


}

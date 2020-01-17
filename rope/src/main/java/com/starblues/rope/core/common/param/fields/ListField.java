package com.starblues.rope.core.common.param.fields;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 列表类型的字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ListField extends AbstractConfigField{

    public static final String FIELD_TYPE = "list";

    public enum Attribute {
        /**
         * 列表数据允许编辑。允许编辑则为组输入框。不允许编辑则为多选框
         */
        ALLOW_CREATE,
    }

    private ListField(Builder builder) {
        super(builder);
    }


    public static Builder toBuilder(String key, String humanName, Boolean defaultValue,
                                    Map<String, String> values){
        Builder builder = new Builder(FIELD_TYPE, key, humanName);
        builder.defaultValue(defaultValue);
        builder.additionalInfo("values", values);
        return builder;
    }


    public static class Builder extends AbstractConfigField.Builder{

        public Builder(String fieldType, String name, String humanName) {
            super(fieldType, name, humanName);
        }

        @Override
        public Builder defaultValue(Object defaultValue) {
            if (defaultValue instanceof List) {
                final List<?> defaultValueList = (List<?>) defaultValue;
                this.defaultValue = defaultValueList.stream()
                        .filter(o -> o instanceof String)
                        .map(String::valueOf)
                        .collect(Collectors.toList());
            }
            return this;
        }

        public Builder attribute(Attribute attribute) {
            if(attribute != null){
                this.attribute(attribute.toString().toLowerCase(Locale.ENGLISH));
            }
            return this;
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
        public ListField build() {
            return new ListField(this);
        }
    }


}

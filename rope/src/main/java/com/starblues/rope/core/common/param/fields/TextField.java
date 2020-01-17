package com.starblues.rope.core.common.param.fields;

import java.util.Locale;

/**
 * 文本类型的字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class TextField extends AbstractConfigField{


    public static final String FIELD_TYPE = "text";

    public enum Attribute {
        /**
         * 是否是密码输入框
         */
        PASSWORD,

        /**
         * 是否是长文本输入框
         */
        TEXTAREA
    }


    private TextField(Builder builder) {
        super(builder);
    }


    public static Builder toBuilder(String key, String humanName, String defaultValue){
        Builder builder = new Builder(FIELD_TYPE, key, humanName);
        builder.defaultValue(defaultValue);
        return builder;
    }


    public static class Builder extends AbstractConfigField.Builder{

        public Builder(String fieldType, String name, String humanName) {
            super(fieldType, name, humanName);
        }

        @Override
        public Builder defaultValue(Object defaultValue) {
            if (defaultValue instanceof String) {
                this.defaultValue = (String) defaultValue;
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

        public Builder attribute(Attribute attribute) {
            if(attribute != null){
                this.attribute(attribute.toString().toLowerCase(Locale.ENGLISH));
            }
            return this;
        }

        @Override
        public TextField build() {
            return new TextField(this);
        }
    }


}

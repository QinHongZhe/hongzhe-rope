package com.starblues.rope.core.common.param.fields;

import java.util.Locale;

/**
 * 数字类型的字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class NumberField extends AbstractConfigField{



    public static final String FIELD_TYPE = "number";

    public enum Attribute {
        /**
         * 只能填正数
         */
        ONLY_POSITIVE,

        /**
         * 只能填负数
         */
        ONLY_NEGATIVE,

        /**
         * 正负数否可以填
         */
        ALL_NUMBER
    }

    private NumberField(Builder builder) {
        super(builder);
    }


    public static Builder toBuilder(String key, String humanName, Number number){
        Builder builder = new Builder(FIELD_TYPE, key, humanName);
        builder.defaultValue(number);
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
            if (defaultValue instanceof Number) {
                this.defaultValue = (Number) defaultValue;
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
        public NumberField build() {
            return new NumberField(this);
        }
    }


}

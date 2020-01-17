package com.starblues.rope.core.common.param.fields;

/**
 * boolean 类型的字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class BooleanField extends AbstractConfigField{

    public static final String FIELD_TYPE = "boolean";


    private BooleanField(Builder builder) {
        super(builder);
    }


    public static Builder toBuilder(String key, String humanName, Boolean defaultValue){
        Builder builder = new Builder(FIELD_TYPE, key, humanName);
        builder.defaultValue(defaultValue);
        return builder;
    }


    public static class Builder extends AbstractConfigField.Builder{

        public Builder(String fieldType, String name, String humanName) {
            super(fieldType, name, humanName);
        }

        @Override
        public AbstractConfigField.Builder defaultValue(Object defaultValue) {
            if (defaultValue instanceof Boolean) {
                this.defaultValue = (Boolean) defaultValue;
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
        public BooleanField build() {
            return new BooleanField(this);
        }
    }

}

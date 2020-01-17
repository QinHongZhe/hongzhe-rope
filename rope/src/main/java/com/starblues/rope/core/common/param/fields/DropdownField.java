package com.starblues.rope.core.common.param.fields;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DropdownField extends AbstractConfigField{

    public static final String FIELD_TYPE = "dropdown";


    private DropdownField(Builder builder) {
        super(builder);
    }

    /**
     * 构建下拉框
     * @param key 下拉框的key
     * @param humanName 下拉框界面展示的名称
     * @param defaultValue 下拉框默认值
     * @param values 下拉框的值。key为 下拉框的key, value 为下拉框界面展示的值
     * @return Builder
     */
    public static Builder toBuilder(String key, String humanName, String defaultValue,
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



        @Override
        public DropdownField build() {
            return new DropdownField(this);
        }
    }


    public static class ValueTemplates {

        public static Map<String, String> timeUnits() {
            Map<String, String> units = Maps.newHashMap();

            for(TimeUnit unit : TimeUnit.values()) {
                String human = unit.toString().toLowerCase(Locale.ENGLISH);
                units.put(unit.toString(), Character.toUpperCase(human.charAt(0)) + human.substring(1));
            }

            return units;
        }

        public static Map<String, String> valueMapFromEnum(Class<? extends Enum> enumClass, Function<Enum, String> valueMapping) {
            return Arrays.stream(enumClass.getEnumConstants()).collect(Collectors.toMap(Enum::toString, valueMapping));
        }

    }

}

package com.starblues.rope.core.common.param.fields;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.starblues.rope.core.common.param.ConfigField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

/**
 * 抽象的配置字段
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractConfigField implements ConfigField {

    private final Builder builder;

    public AbstractConfigField(Builder builder) {
        this.builder = builder;
    }

    @Override
    public String getFieldType() {
        return builder.getFieldType();
    }

    @Override
    public Boolean required() {
        return builder.getRequired();
    }

    @Override
    public String getKey() {
        return builder.getKey();
    }

    @Override
    public String getHumanName() {
        return builder.getHumanName();
    }

    @Override
    public String getDescription() {
        return builder.getDescription();
    }

    @Override
    public Object getDefaultValue() {
        return builder.getDefaultValue();
    }

    @Override
    public Set<String> getAttributes() {
        return builder.getAttributes();
    }

    @Override
    public Map<String, Map<String, String>> getAdditionalInfo() {
        return builder.getAdditionalInfo();
    }



    @Getter(AccessLevel.PROTECTED)
    @ToString
    @EqualsAndHashCode
    public abstract static class Builder{
        protected String fieldType;
        protected String key;
        protected String humanName;

        protected Object defaultValue;
        protected String description;
        protected Boolean required = true;
        protected Set<String> attributes = Sets.newHashSetWithExpectedSize(0);
        protected Map<String, Map<String, String>> additionalInfo = Maps.newHashMapWithExpectedSize(0);



        public Builder(String fieldType, String key, String humanName){
            this.fieldType = fieldType;
            this.key = key;
            this.humanName = humanName;
        }


        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder required(Boolean required){
            this.required = required;
            return this;
        }


        protected Builder attribute(String attribute){
            this.attributes.add(attribute);
            return this;
        }

        protected Builder attributes(Set<String> attributes){
            if(attributes != null){
                this.attributes.addAll(attributes);
            }
            return this;
        }

        protected Builder additionalInfo(String key, String name, String value){
            Map<String, String> info = additionalInfo.get(key);
            if(info == null){
                info = new HashMap<>();
                additionalInfo.put(key, info);
            }
            info.put(name, value);
            return this;
        }

        protected Builder additionalInfo(String key, Map<String, String> values){
            if(values == null){
                return this;
            }
            Map<String, String> info = additionalInfo.get(key);
            if(info == null){
                info = new HashMap<>();
                additionalInfo.put(key, info);
            }
            info.putAll(values);
            return this;
        }

        protected Builder additionalInfo(Map<String, Map<String, String>> values){
            if(values == null){
                return this;
            }
            additionalInfo.putAll(values);
            return this;
        }

        /**
         * 设置默认值
         * @param defaultValue 默认值
         * @return Builder
         */
        public abstract Builder defaultValue(Object defaultValue);

        /**
         * 构建对象
         * @return AbstractConfigField
         */
        public abstract <T extends AbstractConfigField> T build();

    }

}

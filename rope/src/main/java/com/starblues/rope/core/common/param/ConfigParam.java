package com.starblues.rope.core.common.param;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 参数配置接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class ConfigParam {


    private final Map<String, ConfigField> fields = Maps.newLinkedHashMap();


    public void putAll(Map<String, ConfigField> fields) {
        if(fields == null || fields.isEmpty()){
            return;
        }
        this.fields.putAll(fields);
    }

    public ConfigParam addField(ConfigField configField) {
        if(configField == null){
            return this;
        }
        fields.put(configField.getKey(), configField);
        return this;
    }

    public void addFields(List<ConfigField> fields) {
        if(fields == null || fields.isEmpty()){
            return;
        }
        fields.forEach(this::addField);
    }

    public void copyFields(ConfigParam source){
        if(source == null){
            return;
        }
        List<ConfigField> fieldList = source.getFieldList();
        if(fieldList != null){
            addFields(fieldList);
        }
    }


    public boolean containsField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public ConfigField getField(String fieldName) {
        return fields.get(fieldName);
    }

    public Map<String, ConfigField> getFields() {
        return fields;
    }

    public List<ConfigField> getFieldList() {
        return Lists.newArrayList(fields.values());
    }

    @Deprecated
    public boolean removeField(String fieldName) {
        return fields.remove(fieldName) != null;
    }

    public static ConfigParam createWithFields(ConfigField... fields) {
        final ConfigParam configurationRequest = new ConfigParam();
        configurationRequest.addFields(Lists.newArrayList(fields));
        return configurationRequest;
    }

    @JsonValue
    public Map<String, Map<String, Object>> asList() {
        final Map<String, Map<String, Object>> configs = Maps.newLinkedHashMap();

        for (ConfigField f : fields.values()) {
            final Map<String, Object> config = Maps.newHashMap();

            config.put("key", f.getKey());
            config.put("type", f.getFieldType());
            config.put("humanName", f.getHumanName());
            config.put("description", f.getDescription());
            config.put("defaultValue", f.getDefaultValue());
            config.put("required", f.required());
            config.put("attributes", f.getAttributes());
            config.put("additionalInfo", f.getAdditionalInfo());

            configs.put(f.getKey(), config);
        }

        return configs;
    }

    public void check(ConfigParamInfo configuration) throws Exception {
        for (ConfigField field : fields.values()) {
            if (field.required()) {
                final String type = field.getFieldType();
                log.debug("Checking for non-optional field {} of type {} in configuration", field.getKey(), type);
                switch (type) {
                    case BooleanField.FIELD_TYPE:
                        if (!configuration.booleanIsSet(field.getKey())) {
                            throw new Exception("Mandatory configuration field " + field.getKey() + " is missing or has the wrong data type");
                        }
                        break;
                    case NumberField.FIELD_TYPE:
                        if (!configuration.intIsSet(field.getKey())) {
                            throw new Exception("Mandatory configuration field " + field.getKey() + " is missing or has the wrong data type");
                        }
                        break;
                    case TextField.FIELD_TYPE:
                    case DropdownField.FIELD_TYPE:
                        if (!configuration.stringIsSet(field.getKey())) {
                            throw new Exception("Mandatory configuration field " + field.getKey() + " is missing or has the wrong data type");
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unknown field type " + type + ". This is a bug.");
                }
            }
        }
    }


    public ConfigParamInfo filter(ConfigParamInfo config) {
        final Map<String, Object> values = Maps.newHashMap();

        for (final ConfigField field : fields.values()) {
            final String name = field.getKey();
            final String type = field.getFieldType();
            switch (type) {
                case BooleanField.FIELD_TYPE:
                    if (config.booleanIsSet(name)) {
                        values.put(name, config.getBoolean(name));
                    }
                    break;
                case NumberField.FIELD_TYPE:
                    if (config.intIsSet(name)) {
                        values.put(name, config.getInt(name));
                    }
                    break;
                case TextField.FIELD_TYPE:
                case DropdownField.FIELD_TYPE:
                    if (config.stringIsSet(name)) {
                        values.put(name, config.getString(name));
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown field type " + type + ". This is a bug.");
            }
        }
        return new ConfigParamInfo(values);
    }


}

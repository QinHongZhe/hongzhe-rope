package com.starblues.rope.core.common.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starblues.rope.utils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * 配置信息
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ConfigParamInfo implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigParamInfo.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    private static final ConfigParamInfo EMPTY_CONFIGURATION = new ConfigParamInfo(null);

    @JsonProperty
    private final Map<String, Object> source;

    @JsonIgnore
    private final Map<String, String> strings = Maps.newHashMap();

    @JsonIgnore
    private final Map<String, Integer> ints = Maps.newHashMap();

    @JsonIgnore
    private final Map<String, Boolean> bools = Maps.newHashMap();

    @JsonIgnore
    private final Map<String, List<String>> lists = Maps.newHashMap();

    @JsonIgnore
    private final Map<String, List<Map<String, Object>>> listMaps = Maps.newHashMap();

    @JsonCreator
    public ConfigParamInfo(@JsonProperty("source") @Nullable Map<String, Object> source) {
        this.source = firstNonNull(source, Collections.<String, Object>emptyMap());

        for (Map.Entry<String, Object> e : this.source.entrySet()) {
            final String key = e.getKey();
            final Object value = e.getValue();

            if (value == null) {
                LOG.debug("NULL value in configuration key <{}>", key);
                continue;
            }


            if (value instanceof String) {
                strings.put(key, (String) value);
            } else if (value instanceof Integer) {
                ints.put(key, (Integer) value);
            } else if (value instanceof Long) {
                ints.put(key, ((Long) value).intValue());
            } else if (value instanceof Double) {
                ints.put(key, ((Double) value).intValue());
            } else if (value instanceof Boolean) {
                bools.put(key, (Boolean) value);
            } else if (value instanceof List) {
                List listObjects = (List) value;
                List<String> stringList = Lists.newArrayList();
                List<Map<String, Object>> mapList = Lists.newArrayList();
                for (Object object : listObjects) {
                    if(object instanceof String){
                        stringList.add((String) object);
                    } else if(object instanceof Map){
                        mapList.add((Map) object);
                    } else {
                        // TODO 忽略其他类，可根据需求添加
                    }
                }
                if(!stringList.isEmpty()){
                    lists.put(key, stringList);
                }
                if(!mapList.isEmpty()){
                    listMaps.put(key, mapList);
                }
            }else if(value instanceof Map){
                List<Map<String, Object>> mapList = Lists.newArrayList();
                mapList.add((Map)value);
                listMaps.put(key, mapList);
            }else {
                LOG.error("Cannot handle type [{}] of plugin configuration key <{}>.", value.getClass().getCanonicalName(), key);
            }
        }
    }

    @Nullable
    public String getString(String key) {
        return strings.get(key);
    }

    public String getString(String key, String defaultValue) {
        return firstNonNull(strings.get(key), defaultValue);
    }

    public void setString(String key, String value) {
        strings.put(key, value);
    }

    public Integer getInt(String key) {
        return ints.get(key);
    }

    public Integer getInt(String key, int defaultValue) {
        return firstNonNull(ints.get(key), defaultValue);
    }


    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return firstNonNull(bools.get(key), defaultValue);
    }

    public boolean booleanIsSet(String key) {
        return bools.containsKey(key);
    }

    public void setBoolean(String key, boolean value) {
        bools.put(key, value);
    }


    public List<String> getListString(String key) {
        return lists.get(key);
    }

    public List<String> getListString(String key, List<String> defaultValue) {
        return firstNonNull(lists.get(key), defaultValue);
    }

    public List<Map<String, Object>> getListMap(String key) {
        return listMaps.get(key);
    }

    public List<Map<String, Object>> getListMap(String key, List<Map<String, Object>> defaultValue) {
        return firstNonNull(listMaps.get(key), defaultValue);
    }

    /**
     * 字段映射
     * @param fieldKey 获取List<Map> 的key
     * @param mappingKey 映射字段的key
     * @param mappingValue 映射字段的值
     * @return 映射结果
     */
    public Map<String, String> mapping(String fieldKey, String mappingKey, String mappingValue){
        List<Map<String, Object>> listMap = getListMap(fieldKey);
        Map<String, String> fieldMappings = Maps.newHashMap();
        if(listMap != null){
            for (Map<String, Object> map : listMap) {
                Object k = map.get(mappingKey);
                Object v = map.get(mappingValue);
                if(k != null && v != null){
                    fieldMappings.put(Converter.getAsString(k), Converter.getAsString(v));
                }
            }
        }
        return fieldMappings;
    }


    @Nullable
    public Map<String, Object> getSource() {
        return source;
    }

    public Object getSource(String key) {
        return source.get(key);
    }


    public Object getSource(String key, Object defaultValue) {
        return source.getOrDefault(key, defaultValue);
    }


    public boolean stringIsSet(String key) {
        return !isNullOrEmpty(strings.get(key));
    }

    public boolean intIsSet(String key) {
        return ints.containsKey(key);
    }

    @Nullable
    public String serializeToJson() {
        try {
            return source.isEmpty() ? null : OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOG.error("Serializing configuration failed.", e);
            return null;
        }
    }

    public static ConfigParamInfo deserializeFromJson(String json) {
        if (isNullOrEmpty(json)) {
            return EMPTY_CONFIGURATION;
        }

        try {
            return OBJECT_MAPPER.readValue(json, ConfigParamInfo.class);
        } catch (IOException e) {
            LOG.error("Deserializing configuration failed.", e);
            return EMPTY_CONFIGURATION;
        }
    }

}

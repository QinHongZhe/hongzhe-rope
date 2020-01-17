package com.starblues.rope.core.common.param;

import java.util.Map;
import java.util.Set;

/**
 * 配置的字段信息
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ConfigField {

    /**
     * 字段类型
     * @return String
     */
    String getFieldType();

    /**
     * 字段的key
     * @return String
     */
    String getKey();

    /**
     * 字段展示的名称
     * @return String
     */
    String getHumanName();

    /**
     * 字段描述
     * @return String
     */
    String getDescription();

    /**
     * 字段默认值
     * @return Object
     */
    Object getDefaultValue();


    /**
     * 是否必填
     * @return true 必填。false 非必填
     */
    Boolean required();

    /**
     * 属性集合
     * @return 属性集合
     */
    Set<String> getAttributes();

    /**
     * 额外信息
     * @return Map
     */
    Map<String, Map<String, String>> getAdditionalInfo();

}

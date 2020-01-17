package com.starblues.rope.core.common.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

/**
 * 公用的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC )
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CommonConfig {

    /**
     * id
     */
    private String id;

    /**
     * 所需参数集合
     */
    private Map<String, Object> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public <T> T getParam(String key){
        if(params == null){
            return null;
        }
        Object orDefault = params.getOrDefault(key, null);
        if(orDefault == null){
            return null;
        }
        return (T) orDefault;
    }


    @Override
    public String toString() {
        return "CommonConfig{" +
                "id='" + id + '\'' +
                ", params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof CommonConfig)){
            return false;
        }
        CommonConfig that = (CommonConfig) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getParams(), that.getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParams());
    }
}

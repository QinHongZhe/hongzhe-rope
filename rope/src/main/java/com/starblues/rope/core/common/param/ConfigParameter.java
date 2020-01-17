package com.starblues.rope.core.common.param;

/**
 * 配置参数者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ConfigParameter {

    /**
     * 解析配置
     * @param paramInfo 参数配置信息
     * @throws Exception 解析异常
     */
    void parsing(ConfigParamInfo paramInfo) throws Exception;


    /**
     * 配置参数
     * @return 配置参数
     */
    ConfigParam configParam();

}

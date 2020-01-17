package com.starblues.rope.core.converter;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.param.ConfigParameter;

/**
 * 数据转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Converter<Source, Target> extends Identity {


    /**
     * 初始化
     * @param processId 流程id
     * @throws Exception 初始化异常
     */
    void initialize(String processId) throws Exception;

    /**
     * 转换
     * @param source 源数据
     * @return 目标数据
     */
    Target convert(Source source);

    /**
     * 转换器的源类型
     * @return 源类型
     */
    Class<Source> sourceClass();

    /**
     * 转换器的转换的结果类型
     * @return 结果类型
     */
    Class<Target> targetClass();

    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    ConfigParameter configParameter();

}

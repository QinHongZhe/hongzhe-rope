package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;

/**
 * 接受型输入的功能配置参数
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class BaseAcceptInputConfigParameter implements ConfigParameter {


    @Override
    public final void parsing(ConfigParamInfo paramInfo) throws Exception {
        childParsing(paramInfo);
    }


    @Override
    public final ConfigParam configParam() {
        final ConfigParam configParam = new ConfigParam();
        configParam(configParam);
        return configParam;
    }


    /**
     * 子类的解析实现
     * @param paramInfo 参数配置信息
     * @throws Exception 解析异常
     */
    protected abstract void childParsing(ConfigParamInfo paramInfo) throws Exception;

    /**
     * 子类的配置参数
     * @param configParam 配置参数
     */
    protected abstract void configParam(ConfigParam configParam);

}

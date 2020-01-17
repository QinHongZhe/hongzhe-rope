package com.starblues.rope.core.output.writer;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.BooleanField;

/**
 * 写入者的基础公共配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class BaseWriterConfigParameter implements ConfigParameter {

    private final String IS_PARALLEL = "isParallel";

    /**
     * 是否是并行写入
     */
    private Boolean isParallel = true;


    @Override
    public final void parsing(ConfigParamInfo paramInfo) throws Exception {
        isParallel = paramInfo.getBoolean(IS_PARALLEL, true);
        childParsing(paramInfo);
    }


    @Override
    public final ConfigParam configParam() {
        final ConfigParam configParam = new ConfigParam();
        configParam.addField(BooleanField.toBuilder(IS_PARALLEL, "是否并行写入", true)
                .description("并行写入会提高写入速度, 但非线程安全, 请保证写入实现者为线程安全。")
                .required(true)
                .build());
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


    public final Boolean getParallel() {
        return isParallel;
    }
}

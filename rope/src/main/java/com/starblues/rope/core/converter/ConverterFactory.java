package com.starblues.rope.core.converter;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 转换器工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class ConverterFactory {

    private final PluginApplication pluginApplication;
    private final ProcessFactory processFactory;

    public ConverterFactory(PluginApplication pluginApplication,
                            ProcessFactory processFactory) {
        this.pluginApplication = pluginApplication;
        this.processFactory = processFactory;
    }

    /**
     * 得到数据转换器
     * @param processId 流程id
     * @param sourceClass 转换源数据类型
     * @param targetClass 转换后的数据类型
     * @return 转换器
     */
    public Converter getConverter(String processId, Class<?> sourceClass, Class<?> targetClass){
        CommonConfig converterConfig = getConverterConfig(processId);
        if(converterConfig == null){
            return null;
        }
        PluginUser pluginUser = pluginApplication.getPluginUser();
        Converter converter = CommonUtils.getImpls(pluginUser, Converter.class, impl->{
            return Objects.equals(impl.id(), converterConfig.getId()) &&
                    Objects.equals(impl.sourceClass(), sourceClass) &&
                    Objects.equals(impl.targetClass(), targetClass);
        });
        initConverter(processId, converterConfig, converter);
        return converter;
    }

    /**
     * 得到输入数据转换器
     * @param processId 流程id
     * @param sourceClass 转换源数据类型
     * @return 转换器
     */
    public AbstractInputConverter getInputConverter(String processId, Class<?> sourceClass){
        if(sourceClass == null){
            return null;
        }
        CommonConfig converterConfig = getConverterConfig(processId);
        if(converterConfig == null){
            return null;
        }
        PluginUser pluginUser = pluginApplication.getPluginUser();
        AbstractInputConverter inputConverter = CommonUtils.getImpls(pluginUser, AbstractInputConverter.class, impl->{
            return Objects.equals(impl.id(), converterConfig.getId()) &&
                    Objects.equals(impl.sourceClass(), sourceClass) &&
                    Objects.equals(impl.targetClass(), Record.class);
        });
        initConverter(processId, converterConfig, inputConverter);
        return inputConverter;
    }

    /**
     * 得到输入数据转换器
     * @param processId 流程id
     * @param targetClass 转换后的数据类型
     * @return 转换器
     */
    public AbstractWriterConverter getWriteConverter(String processId, Class<?> targetClass){
        CommonConfig converterConfig = getConverterConfig(processId);
        if(converterConfig == null){
            return null;
        }
        PluginUser pluginUser = pluginApplication.getPluginUser();
        AbstractWriterConverter writerConverter = CommonUtils.getImpls(pluginUser, AbstractWriterConverter.class, impl->{
            return Objects.equals(impl.id(), converterConfig.getId()) &&
                    Objects.equals(impl.sourceClass(), Record.class) &&
                    Objects.equals(impl.targetClass(), targetClass);
        });
        initConverter(processId, converterConfig, writerConverter);
        return writerConverter;
    }

    /**
     * 得到转换器的配置
     * @param processId 流程id
     * @return CommonConfig
     */
    private CommonConfig getConverterConfig(String processId){
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager == null){
            throw new RuntimeException("The process " + processId + " cannot be found");
        }
        ProcessConfig processConfig = processManager.getProcessConfig();
        ProcessConfig.InputConfig input = processConfig.getInput();
        CommonConfig converterConfig = input.getConverter();
        if(converterConfig == null || StringUtils.isEmpty(converterConfig.getId())){
            return null;
        }
        return converterConfig;
    }


    /**
     * 初始化转换器参数
     * @param processId 流程id
     * @param converterConfig 转换器的配置
     * @param converter 转换器
     */
    private void initConverter(String processId, CommonConfig converterConfig, Converter converter){
        if(converter == null){
            return;
        }
        try {
            ConfigParameter configParameter = converter.configParameter();
            CommonUtils.parsingConfig(configParameter, converterConfig.getParams());
            converter.initialize(processId);
        } catch (Exception e) {
            log.error("Initialize process '{}' converter '{}' param failure. {}",
                    converterConfig.getId(), converter.id(), e.getMessage(), e);
        }
    }

}

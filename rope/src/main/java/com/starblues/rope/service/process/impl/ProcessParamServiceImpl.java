package com.starblues.rope.service.process.impl;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.google.common.collect.Lists;
import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.converter.AbstractWriterConverter;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.input.AbstractReaderInput;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.input.support.accept.AbstractAcceptInput;
import com.starblues.rope.core.output.AbstractOutput;
import com.starblues.rope.core.output.writer.Writer;
import com.starblues.rope.service.process.ProcessParamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class ProcessParamServiceImpl implements ProcessParamService {

    private static final String READER_INPUT = "readerInput";
    private static final String ACCEPT_INPUT = "acceptInput";

    private static final String OUTPUT = "output";
    private static final String READER = "reader";
    private static final String WRITER = "writer";

    private static final String INPUT_CONVERTER = "inputConverter";
    private static final String WRITER_CONVERTER = "writerConverter";

    private final PluginApplication pluginApplication;

    public ProcessParamServiceImpl(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
    }


    @Override
    public List<ParamInfo> getInputConfigParam() {
        List<ParamInfo> paramInfos = Lists.newArrayList();

        List<AbstractReaderInput> abstractReaderInputs = pluginUser().getBeans(AbstractReaderInput.class);

        for (AbstractReaderInput abstractReaderInput : abstractReaderInputs) {
            paramInfos.add(getParamInfo(abstractReaderInput, READER_INPUT, abstractReaderInput.configParameter()));
        }

        List<AbstractAcceptInput> abstractAcceptInputs = pluginUser().getBeans(AbstractAcceptInput.class);
        for (AbstractAcceptInput abstractAcceptInput : abstractAcceptInputs) {
            paramInfos.add(getParamInfo(abstractAcceptInput, ACCEPT_INPUT, abstractAcceptInput.configParameter()));
        }

        return paramInfos;
    }

    @Override
    public List<ParamInfo> getOutputConfigParam() {
        List<ParamInfo> paramInfos = Lists.newArrayList();
        List<AbstractOutput> abstractOutputs = pluginUser().getBeans(AbstractOutput.class);
        for (AbstractOutput abstractOutput : abstractOutputs) {
            paramInfos.add(getParamInfo(abstractOutput, OUTPUT, abstractOutput.configParameter()));
        }
        return paramInfos;
    }

    @Override
    public List<ParamInfo> getWriterConfigParam() {
        List<ParamInfo> paramInfos = Lists.newArrayList();
        List<Writer> writers = pluginUser().getBeans(Writer.class);
        for (Writer writer : writers) {
            paramInfos.add(getParamInfo(writer, WRITER, writer.configParameter()));
        }
        return paramInfos;
    }

    @Override
    public List<ParamInfo> getReaderConfigParam() {
        List<ParamInfo> paramInfos = Lists.newArrayList();
        List<Reader> readers = pluginUser().getBeans(Reader.class);
        for (Reader reader : readers) {
            paramInfos.add(getParamInfo(reader, READER, reader.configParameter()));
        }
        return paramInfos;
    }




    @Override
    public List<ParamInfo> getDataHandlerConfigParam() {
        List<ParamInfo> paramInfos = Lists.newArrayList();
        List<DateHandler> dataHandlers = pluginUser().getBeans(DateHandler.class);
        for (DateHandler dataHandler : dataHandlers) {
            paramInfos.add(getParamInfo(dataHandler, READER, dataHandler.configParameter()));
        }
        return paramInfos;
    }

    @Override
    public List<ConverterParamInfo> getInputConverterConfigParam() {
        List<ConverterParamInfo> paramInfos = Lists.newArrayList();
        List<AbstractInputConverter> inputConverters = pluginUser().getBeans(AbstractInputConverter.class);
        for (AbstractInputConverter inputConverter : inputConverters) {
            ParamInfo paramInfo = getParamInfo(inputConverter, INPUT_CONVERTER,
                    inputConverter.configParameter());
            ConverterParamInfo converterParamInfo = new ConverterParamInfo();
            BeanUtils.copyProperties(paramInfo, converterParamInfo);
            converterParamInfo.setConvertClass(inputConverter.sourceClass());
            paramInfos.add(converterParamInfo);
        }
        return paramInfos;
    }

    @Override
    public List<ConverterParamInfo> getWriterConverterConfigParam() {
        List<ConverterParamInfo> paramInfos = Lists.newArrayList();
        List<AbstractWriterConverter> writerConverters = pluginUser().getBeans(AbstractWriterConverter.class);
        for (AbstractWriterConverter writerConverter : writerConverters) {
            ParamInfo paramInfo = getParamInfo(writerConverter, WRITER_CONVERTER,
                    writerConverter.configParameter());
            ConverterParamInfo converterParamInfo = new ConverterParamInfo();
            BeanUtils.copyProperties(paramInfo, converterParamInfo);
            converterParamInfo.setConvertClass(writerConverter.targetClass());
            paramInfos.add(converterParamInfo);
        }
        return paramInfos;
    }


    /**
     * 生产参数对象
     * @param identity identity接口
     * @param type 类型
     * @param configParameter 参数信息
     * @return ParamInfo
     */
    private ParamInfo getParamInfo(Identity identity, String type, ConfigParameter configParameter){
        ParamInfo inputInfo = new ParamInfo();
        inputInfo.setId(identity.id());
        inputInfo.setName(identity.name());
        inputInfo.setType(type);
        inputInfo.setDescription(identity.describe());

        if(configParameter != null){
            inputInfo.setConfigParam(configParameter.configParam());
        }

        return inputInfo;
    }

    private PluginUser pluginUser(){
        return pluginApplication.getPluginUser();
    }


}

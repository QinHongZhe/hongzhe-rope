package com.starblues.rope.core.output;

import com.gitee.starblues.integration.user.PluginUser;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.converter.AbstractWriterConverter;
import com.starblues.rope.core.output.writer.AbstractConverterWriter;
import com.starblues.rope.core.output.writer.Writer;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.utils.CommonUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static com.starblues.rope.utils.CommonUtils.getImpls;

/**
 * 输出工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class OutputFactory {

    private final ExecutorService executorService;
    private final PluginUser pluginUser;

    public OutputFactory(OutputPollConfig pollConfig,
                         PluginUser pluginUser){
        this.pluginUser = pluginUser;
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Output-Writer-Pool-%d")
                .build();
        if(pollConfig == null){
            pollConfig = new OutputPollConfig();
        }
        this.executorService = new ThreadPoolExecutor(
                pollConfig.getCorePoolSize(),
                pollConfig.getMaximumPoolSize(),
                pollConfig.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }


    /**
     * 得到输出。
     * @param processId 流程id
     * @param outputConfig 输出配置
     * @return 输出
     * @throws Exception 获取输出抛出的异常
     */
    public Output getOutput(String processId, ProcessConfig.OutputConfig outputConfig) throws Exception {
        if(StringUtils.isEmpty(processId)){
            throw new IllegalArgumentException("ProcessInfo id can't be null");
        }
        if(outputConfig == null){
            throw new IllegalArgumentException("ProcessInfo output config can't be null");
        }
        String outputId = outputConfig.getId();
        if(StringUtils.isEmpty(outputId)){
            throw new IllegalArgumentException("Output id can't be empty");
        }
        List<ProcessConfig.WriterConfig> writers = outputConfig.getWriters();

        Output output = null;

        AbstractOutput abstractOutput = getImpls(pluginUser, AbstractOutput.class, impl -> {
            return Objects.equals(impl.id(), outputId);
        });

        if(abstractOutput != null){
            abstractOutput = pluginUser.generateNewInstance(abstractOutput);
            abstractOutput.setProcessId(processId);
            abstractOutput.setExecutorService(executorService);
            output = abstractOutput;
        } else {
            throw new Exception("Not found output '" + outputId + "'");
        }


        ConfigParameter configParameter = output.configParameter();
        if(configParameter != null){
            CommonUtils.parsingConfig(configParameter, outputConfig.getParams());
        }

        if(writers != null && !writers.isEmpty()){
            for (ProcessConfig.WriterConfig writerConfig : writers) {
                Output.WriterWrapper writerWrapper = getWriteAndInit(processId, pluginUser, writerConfig);
                output.addWriter(writerWrapper);
            }
        }
        // 初始化输出
        output.initialize();
        return output;
    }



    /**
     * 获取数据写入者, 并对写入者进行初始化
     * @param processId 流程id
     * @param pluginUser pluginUser
     * @param writerConfig 数据写入者的配置
     * @return 数据写入者实例
     * @throws Exception 产生的异常
     */
    private Output.WriterWrapper getWriteAndInit(String processId, PluginUser pluginUser,
                                                 ProcessConfig.WriterConfig writerConfig) throws Exception {
        if(writerConfig == null){
            throw new NullPointerException("ProcessInfo output write config can't be null");
        }
        String writerId = writerConfig.getId();
        Writer writer = getImpls(pluginUser, Writer.class, impl -> {
            return Objects.equals(impl.id(), writerId);
        });
        if(writer == null){
            throw new Exception("Not found writer : '" + writerId + "'");
        }
        // 生成权限的写入者。如果为单例，则多个流程会公用同一个写入者。会导致数据错误。
        writer = pluginUser.generateNewInstance(writer);
        setWriterConverter(writer, pluginUser, writerConfig);
        // 初始化数据写入者
        ConfigParameter configParameter = writer.configParameter();
        CommonUtils.parsingConfig(configParameter, writerConfig.getParams());
        writer.initialize(processId);

        Output.WriterWrapper writerWrapper = new Output.WriterWrapper();
        writerWrapper.setWriter(writer);
        writerWrapper.setCode(writerConfig.getCode());
        return writerWrapper;
    }

    /**
     * 设置写入者的的转换器。如果写入者是 AbstractWriterConverter 类型, 才可配置
     * @param writer 写入者
     * @param pluginUser pluginUser
     * @param writerConfig 数据写入者的配置
     */
    private void setWriterConverter(Writer writer,
                                    PluginUser pluginUser,
                                    ProcessConfig.WriterConfig writerConfig) throws Exception{
        if(!(writer instanceof AbstractConverterWriter)){
            return;
        }
        // 如果该写入者是可配置转换器的写入者, 则进行配置转换器
        CommonConfig commonConfig = writerConfig.getConverter();
        AbstractWriterConverter writerConverter = null;
        if(commonConfig != null && !StringUtils.isEmpty(commonConfig.getId())){
            String converterId = commonConfig.getId();
            writerConverter = getImpls(pluginUser, AbstractWriterConverter.class, impl->{
                return Objects.equals(impl.id(), converterId);
            });
            if(writerConverter == null){
                throw new Exception("Not found this writer converter '" + converterId + "'");
            }
            if(!writer.support(writerConverter)){
                throw new Exception("The writer '" + writer.id() +
                        "' does not support converter '" + converterId + "'");
            }
            ((AbstractConverterWriter) writer).setWriterConverter(writerConverter);
        }
    }


    @Data
    @Builder
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class OutputPollConfig{

        @Builder.Default
        private int corePoolSize = 100;

        @Builder.Default
        private int maximumPoolSize = 1024;

        @Builder.Default
        private long keepAliveTimeMs = 0L;


    }
}

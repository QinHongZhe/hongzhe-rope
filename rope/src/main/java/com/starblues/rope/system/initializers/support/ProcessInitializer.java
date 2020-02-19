package com.starblues.rope.system.initializers.support;

import com.starblues.rope.config.configuration.RopeCoreConfiguration;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.process.config.Configuration;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.process.store.ProcessStorageFactory;
import com.starblues.rope.system.initializers.AbstractInitializer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 流程初始化
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class ProcessInitializer extends AbstractInitializer {

    private final RopeCoreConfiguration config;
    private final ProcessStorageFactory processStorageFactory;
    private final ProcessFactory processFactory;

    public ProcessInitializer(RopeCoreConfiguration configuration,
                              ProcessFactory processFactory,
                              ProcessStorageFactory processStorageFactory) {
        this.config = configuration;
        this.processStorageFactory = processStorageFactory;
        this.processFactory = processFactory;
    }


    @Override
    protected String name() {
        return "process";
    }

    @Override
    protected void start() throws Exception {
        Configuration configuration = Configuration.builder()
                .name(config.getName())
                .inputConfig(config.getInputManager())
                .outputConfig(config.getOutputManager())
                .outputPollConfig(config.getOutputPoll())
                .inputTransport(getCommonConfig(config.getInputTransport()))
                .outputTransport(getCommonConfig(config.getOutputTransport()))
                .build();
        processFactory.start(configuration);
        processStorageFactory.initialize();
    }

    @Override
    protected void stop() throws Exception {
        log.info("Start stop process...");
        try {
            processFactory.stop();
            log.info("Stop process success");
        } catch (Exception e) {
            log.info("Stop process failure. {}", e.getMessage(), e);
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    /**
     * 通过 DataTransferConfiguration.Transport 获取 CommonConfig 配置
     * @param transport transport
     * @return CommonConfig
     */
    private CommonConfig getCommonConfig(RopeCoreConfiguration.Transport transport){
        return CommonConfig.builder()
                .id(transport.getId())
                .params(transport.getParams())
                .build();
    }
}

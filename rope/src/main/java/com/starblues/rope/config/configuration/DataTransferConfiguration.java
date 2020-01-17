package com.starblues.rope.config.configuration;

import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.output.OutputFactory;
import com.starblues.rope.core.output.OutputManagerConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 输入配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "data-transfer")
public class DataTransferConfiguration {

    @Value("${name:dataTransfer}")
    private String name;

    /**
     * 输入管理器的配置
     */
    private InputManagerConfig inputManager;

    /**
     * 输出管理器的配置
     */
    private OutputManagerConfig outputManager;

    /**
     * 输出线程池的配置
     */
    private OutputFactory.OutputPollConfig outputPoll;

    /**
     * 输入的 Transport 配置
     */
    private Transport inputTransport;

    /**
     * 输出的 Transport 配置
     */
    private Transport outputTransport;

    /**
     * 流程存储
     */
    private List<ProcessStorageConfig> processStorages;

    /**
     * Transport 的配置Bean
     */
    public static class Transport{

        private String id = "default";
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
    }

    /**
     * 流程存储的类型
     */
    @Data
    public static class ProcessStorageConfig{

        /**
         * 流程存储的id。目前支持：file、classpath
         */
        private String id;

        /**
         * 参数
         */
        private Map<String, Object> params;
    }


}

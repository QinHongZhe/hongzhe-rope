package com.starblues.rope.core.common.config;

import lombok.*;

import java.io.Serializable;
import java.util.*;

/**
 * 流程配置bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC )
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ProcessConfig implements Serializable {

    /**
     * 流程id
     */
    private String processId;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 是否启动
     */
    @Builder.Default
    private Boolean isStart = true;

    /**
     * 流程的输入配置
     */
    private InputConfig input;

    /**
     * 流程的数据处理流配置
     */
    private List<CommonConfig> dateHandlerFlow;

    /**
     * 流程的输出配置
     */
    private OutputConfig output;

    /**
     * 流程的输入配置bean
     */
    @Data
    public static class InputConfig extends CommonConfig{

        /**
         * 该输入的数据读取者的配置
         */
        private CommonConfig reader;

        /**
         * 输入转换器
         */
        private CommonConfig converter;

    }

    /**
     * 该流程的输出配置bean
     */
    @Data
    public static class OutputConfig extends CommonConfig{
        /**
         * 该流程的写入者配置。可配置多个
         */
        private List<WriteConfig> writers;

    }

    /**
     * 写入者配置
     */
    @Data
    public static class WriteConfig extends CommonConfig{

        /**
         * 写入者的数据转换器
         */
        private CommonConfig converter;

        /**
         * 自定义写入者code. 同一个输出的写入者code不能相同
         */
        private String code;
    }


}

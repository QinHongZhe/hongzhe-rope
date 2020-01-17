package com.starblues.rope.config.constant;

/**
 * 指标度量的常量
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class MetricsConstant {

    private MetricsConstant(){

    }


    public static final String RATE_SUFFIX = "1-sec-rate";


    /**
     * 特定输入中读取者的名称前缀
     */
    public static final String GROUP_INPUT_READER = "input-reader";

    /**
     * 默认的transport分组：transport
     */
    public static final String GROUP_TRANSPORT = "transport";

    /**
     * 输入的transport分组
     */
    public static final String GROUP_INPUT_TRANSPORT = "input-transport";


    /**
     * 输出数据分组
     */
    public static final String GROUP_OUTPUT_TRANSPORT = "output-transport";


    /**
     * 输出数据输出写入者分组
     */
    public static final String GROUP_OUTPUT_WRITER = "output-writer";

}

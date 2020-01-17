package com.starblues.rope.core.output.writer;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.converter.Converter;
import com.starblues.rope.core.model.record.Record;

import java.util.List;

/**
 * 数据写入者的接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Writer<ConvertData> extends Identity {

    /**
     * 初始化
     * @param processId 流程id
     * @throws Exception 初始化异常
     */
    void initialize(String processId) throws Exception;


    /**
     * 是否支持该转换器
     * @param converter 数据转换器
     * @return 支持返回true. 不支持返回false
     */
    boolean support(Converter converter);

    /**
     * 转换记录
     * @param record 传入的记录
     * @return 转换后的结构
     * @throws Exception 转换数据出错
     */
    ConvertData convert(Record record) throws Exception;

    /**
     * 转换后的数据Class
     * @return OutputDataWrapper.class
     */
    Class<ConvertData> convertDataClass();


    /**
     * 数据写入
     * @param convertDataList 转换后的数据记录集合
     * @throws Exception 写入异常
     */
    void write(List<ConvertData> convertDataList) throws Exception;


    /**
     * 销毁时调用。只调用一次
     * @throws Exception 销毁异常
     */
    void destroy() throws Exception;

    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    BaseWriterConfigParameter configParameter();

}

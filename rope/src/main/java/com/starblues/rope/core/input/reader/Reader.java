package com.starblues.rope.core.input.reader;

import com.starblues.rope.core.common.Identity;

/**
 * 读取数据者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Reader extends Identity {

    /**
     * 初始化
     * @param processId 流程id。当前写入者作用于的流程id
     * @throws Exception 初始化异常
     */
    void initialize(String processId) throws Exception;


    /**
     * 读取数据
     * @param consumer 数据消费者
     * @throws Exception 读取异常
     */
    void reader(Consumer consumer) throws Exception;

    /**
     * 停止时调用
     * @throws Exception 销毁异常
     */
    void destroy() throws Exception;

    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    BaseReaderConfigParameter configParameter();

}

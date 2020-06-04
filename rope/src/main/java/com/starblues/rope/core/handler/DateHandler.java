package com.starblues.rope.core.handler;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.record.Record;

/**
 * 数据转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface DateHandler extends Identity {

    /**
     * 初始化
     * @param processId 流程id
     * @throws Exception 初始化异常
     * @return 初始化结果。初始化失败, 或者异常, 则将该数据处理者不加入处理流中
     */
    boolean initialize(String processId) throws Exception;


    /**
     * 处理数据
     * @param record 输入的记录
     * @return 处理后输入的记录
     * @throws Exception 转换异常
     */
    Record handle(Record record) throws Exception;

    /**
     * 停止时调用
     * @throws Exception 销毁异常
     */
    void destroy() throws Exception;

    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    ConfigParameter configParameter();

}

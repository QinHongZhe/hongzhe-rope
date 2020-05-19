package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.input.AbstractInput;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import org.springframework.util.StringUtils;

/**
 * 抽象的接受数据的输入。等待别人给其发送数据
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractAcceptInput extends AbstractInput {

    private String processId;

    @Override
    public final void setProcessId(String processId) {
        if(StringUtils.isEmpty(processId)){
            return;
        }
        this.processId = processId;
    }


    @Override
    public final String processId() {
        return this.processId;
    }


    @Override
    protected void startBefore(Consumer consumer) throws Exception {
        if(StringUtils.isEmpty(processId)){
            throw new Exception("Start acceptInput failure, processId can't be empty");
        }
        toStart(consumer);
    }


    /**
     * 子类实现启动
     * @param consumer 数据消费者
     * @throws Exception 启动异常
     */
    protected abstract void toStart(Consumer consumer) throws Exception;

    /**
     * 子类实现停止
     *  @throws Exception 停止抛出的异常
     */
    @Override
    protected abstract void toStop() throws Exception;


    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    @Override
    public abstract BaseAcceptInputConfigParameter configParameter();



}

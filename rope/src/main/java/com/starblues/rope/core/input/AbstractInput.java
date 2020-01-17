package com.starblues.rope.core.input;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.input.reader.AbstractStateConsumer;
import com.starblues.rope.core.input.reader.Consumer;
import com.starblues.rope.core.input.reader.DoNotStateConsumer;
import com.starblues.rope.core.input.reader.TransportConsumer;
import com.starblues.rope.core.transport.Transport;
import org.springframework.util.StringUtils;


/**
 * 抽象的输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractInput implements Input{

    private final StateControl stateControl = new StateControl();

    private String processId;
    private Transport transport;
    private AbstractStateConsumer abstractStateConsumer;
    private DoNotStateConsumer consumer;

    public void setProcessId(String processId) {
        if(StringUtils.isEmpty(this.processId) && !StringUtils.isEmpty(processId)){
            this.processId = processId;
        }
    }

    @Override
    public String processId() {
        return processId;
    }

    @Override
    public final void start(Transport transport) throws Exception {
        stateControl.start();
        try {
            this.transport = transport;
            this.abstractStateConsumer = getAbstractStateConsumer();
            this.abstractStateConsumer.start();
            // 将它包装成不可操作的数据消费者
            consumer = new DoNotStateConsumer(abstractStateConsumer);
            startBefore(consumer);
            stateControl.startSuccessful();
            startAfter(consumer);
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }



    @Override
    public final void stop() throws Exception {
        stateControl.stop();
        try {
            toStop();
            transport = null;
            if(abstractStateConsumer != null){
                abstractStateConsumer.stop();
            }
            stateControl.stopSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }


    @Override
    public final State state() {
        return stateControl.getCurrentState();
    }

    /**
     * 启动之前. 子类可重写
     * @param consumer 数据消费者
     * @throws Exception 操作异常
     */
    protected void startBefore(Consumer consumer) throws Exception{

    }

    /**
     * 启动之后. 子类可重写
     * @param consumer 数据消费者
     * @throws Exception 操作异常
     */
    protected void startAfter(Consumer consumer) throws Exception{

    }

    /**
     * 子类对停止操作的实现. 子类可重写
     * @throws Exception 停止抛出的异常
     */
    protected void toStop() throws Exception{

    }

    private AbstractStateConsumer getAbstractStateConsumer(){
        return new TransportConsumer(processId(), transport);
    }

    protected Transport getTransport() {
        return transport;
    }


    protected final Consumer getConsumer(){
        return consumer;
    }


}

package com.starblues.rope.core.input.reader.consumer;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;


/**
 * 抽象的有状态的消费者。可控制启动，停止
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractStateConsumer implements Consumer {

    private final StateControl stateControl = new StateControl();


    public final void start() throws Exception {
        stateControl.start();
        try {
            startup();
            stateControl.startSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }

    public final void stop() throws Exception{
        stateControl.stop();
        try {
            shutdown();
            stateControl.stopSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }


    @Override
    public void accept(Record record) {
        if(record == null){
            return;
        }
        check();
        acceptImpl(record);
    }


    @Override
    public void accept(RecordGroup recordGroup) {
        if(recordGroup == null || recordGroup.size() == 0){
            return;
        }
        check();
        acceptImpl(recordGroup);
    }



    /**
     * 启动的操作
     * @throws Exception 启动异常
     */
    protected abstract void startup() throws Exception;

    /**
     * 停止的操作
     * @throws Exception 停止异常
     */
    protected abstract void shutdown() throws Exception;


    /**
     * 接受数据的实现。由子类实现
     * @param record 数据bean
     */
    protected abstract void acceptImpl(Record record);



    /**
     * 接受数据集合的实现。由子类实现
     * @param recordGroup 记录组
     */
    protected abstract void acceptImpl(RecordGroup recordGroup);



    private void check(){
        if(stateControl.getCurrentState() != State.RUNNING){
            throw new RuntimeException("Current consumer not running, cannot accept data");
        }
    }

}

package com.starblues.rope.core.common;

import java.util.function.BooleanSupplier;

/**
 * 状态控制器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public final class StateControl {

    /**
     * 当前状态
     */
    private State currentState = State.NEW;
    /**
     * 上一状态
     */
    private State preState  = State.NEW;

    /**
     * 错误状态异常信息
     */
    private volatile Throwable throwable;

    public void start(){
        changeState(State.STARTING, ()->{
            return currentState == State.NEW || currentState == State.STOP || currentState == State.FAILED;
        });
    }

    public synchronized void startSuccessful(){
        changeState(State.RUNNING, ()->{
            return currentState == State.STARTING;
        });
    }

    public void stop(){
        synchronized (this){
            if(currentState == State.NEW || currentState == State.FAILED){
                return;
            }
        }
        changeState(State.STOPPING, ()->{
            return currentState == State.RUNNING || currentState == State.STARTING;
        });
    }

    public void stopSuccessful(){
        changeState(State.STOP, ()->{
            return currentState == State.STOPPING;
        });
    }

    public void throwable(Throwable throwable){
        changeState(State.FAILED, ()->{
            return true;
        });
        this.throwable = throwable;
    }


    public synchronized State getCurrentState(){
        return currentState;
    }


    public synchronized State getPreState() {
        return preState;
    }

    public synchronized Throwable getThrowable() {
        return throwable;
    }

    /**
     * 改变状态
     * @param enterState 要进入的状态
     * @param conditions 进入状态的条件
     */
    private synchronized void changeState(State enterState,
                                          BooleanSupplier conditions){
        if(conditions.getAsBoolean()){
            this.preState = currentState;
            this.currentState = enterState;
        } else {
            throw new RuntimeException("Current state is " + currentState + ", can not enter state[" + enterState+ "]");
        }
    }

}

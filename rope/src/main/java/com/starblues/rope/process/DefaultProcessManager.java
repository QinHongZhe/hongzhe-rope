package com.starblues.rope.process;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.common.manager.Manager;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.handler.DateHandlerFactory;
import com.starblues.rope.core.input.DoNotOperateInput;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.InputFactory;
import com.starblues.rope.core.input.support.accept.AbstractHttpAcceptInput;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.output.DoNotOperateOutput;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.output.OutputFactory;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.exception.ExceptionGroup;

import java.util.List;
import java.util.Objects;

/**
 * 配置文件流程管理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultProcessManager implements ProcessManager{

    private final StateControl stateControl = new StateControl();

    private final ProcessConfig processConfig;

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final DateHandlerFactory dateHandlerFactory;

    private final Manager<Input> inputManager;
    private final Manager<Output> outputManager;


    private Input input;
    private Output output;
    private DateHandler dateHandler;


    public DefaultProcessManager(ProcessConfig processConfig,
                                 InputFactory inputFactory,
                                 OutputFactory outputFactory,
                                 DateHandlerFactory dateHandlerFactory,
                                 Manager<Input> inputManager,
                                 Manager<Output> outputManager) {
        this.processConfig = Objects.requireNonNull(processConfig);


        this.inputFactory = Objects.requireNonNull(inputFactory);
        this.outputFactory = Objects.requireNonNull(outputFactory);
        this.dateHandlerFactory = Objects.requireNonNull(dateHandlerFactory);

        this.inputManager = Objects.requireNonNull(inputManager);
        this.outputManager = Objects.requireNonNull(outputManager);
    }


    @Override
    public void start() throws Exception {
        stateControl.start();
        // 先启动输出。
        try {
            startOutput();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
        // 再启动数据处理者
        try {
            this.dateHandler = dateHandlerFactory.getDateHandler(
                    processConfig.getProcessId(),
                    processConfig.getDateHandlerFlow());
        } catch (Exception e){
            stateControl.throwable(e);
            // 出现异常后, 停止输出
            output.stop();
            throw e;
        }
        // 最后启动输入
        try {
            startInput();
            stateControl.startSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            // 输如出现异常后, 销毁数据处理器、和输出
            try {
                dateHandler.destroy();
            } catch (Exception e1){
                e1.printStackTrace();
            }
            try {
                output.stop();
            } catch (Exception e2){
                e2.printStackTrace();
            }
            throw e;
        }
    }

    /**
     * 启动输入
     * @throws Exception 启动异常
     */
    private void startInput() throws Exception {
        input = inputFactory.getInput(processConfig.getProcessId(), processConfig.getInput());
        inputManager.start(input);
    }

    /**
     * 启动输出
     * @throws Exception 启动异常
     */
    private void startOutput() throws Exception{
        output = outputFactory.getOutput(processConfig.getProcessId(), processConfig.getOutput());
        outputManager.start(output);
    }



    @Override
    public void stop() throws Exception {
        if(state() == State.NEW){
            // 如果是新建态, 则直接返回
            return;
        }
        stateControl.stop();

        ExceptionGroup exceptionGroup = new ExceptionGroup();
        // 先停止输入
        try {
            inputManager.stop(input.processId());
        } catch (Exception e){
            exceptionGroup.addException(e);
        } finally {
            input = null;
        }

        // 再停止数据处理器
        try {
            dateHandler.destroy();
        } catch (Exception e){
            exceptionGroup.addException(e);
        } finally {
            dateHandler = null;
        }

        // 最后停止输出
        try {
            outputManager.stop(output.processId());
            stateControl.stopSuccessful();
        } catch (Exception e){
            exceptionGroup.addException(e);
        } finally {
            output = null;
        }

        if(exceptionGroup.isHaveException()){
            stateControl.throwable(exceptionGroup);
            throw exceptionGroup;
        }
    }

    @Override
    public String getProcessId() {
        return processConfig.getProcessId();
    }

    @Override
    public ProcessConfig getProcessConfig() {
        return processConfig;
    }

    @Override
    public State state() {
        return stateControl.getCurrentState();
    }

    @Override
    public Input getInput() {
        checkRunning();
        if(input instanceof AbstractHttpAcceptInput){
            return input;
        }
        return new DoNotOperateInput(input);
    }

    @Override
    public DateHandler getDateHandler() {
        if(state() == State.RUNNING){
            return dateHandler;
        } else {
            return null;
        }
    }

    @Override
    public Output getOutput() {
        if(state() == State.RUNNING){
            return new DoNotOperateOutput(this.output);
        } else {
            return new EmptyOutput(getProcessId());
        }
    }

    /**
     * 检查是否运行
     */
    private void checkRunning(){
        if(state() != State.RUNNING){
            throw new RuntimeException("The process " + processConfig.getProcessId() + " is not started");
        }
    }


    private class EmptyOutput implements Output{

        private final String processId;

        private EmptyOutput(String processId) {
            this.processId = processId;
        }

        @Override
        public void addWriter(WriterWrapper writerWrapper) {

        }

        @Override
        public void addWriter(List<WriterWrapper> writerWrappers) {

        }

        @Override
        public List<WriterWrapper> getWriter() {
            return null;
        }

        @Override
        public void start() throws Exception {

        }

        @Override
        public void stop() throws Exception {

        }

        @Override
        public void output(RecordWrapper recordWrapper) {

        }

        @Override
        public String processId() {
            return this.processId;
        }

        @Override
        public void initialize() throws Exception {

        }

        @Override
        public State state() {
            return State.RUNNING;
        }

        @Override
        public ConfigParameter configParameter() {
            return null;
        }

        @Override
        public String id() {
            return "empty-output";
        }

        @Override
        public String name() {
            return "empty-output";
        }

        @Override
        public String describe() {
            return null;
        }
    }


}

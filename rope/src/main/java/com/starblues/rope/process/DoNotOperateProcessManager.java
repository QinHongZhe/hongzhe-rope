package com.starblues.rope.process;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.common.config.ProcessConfig;

/**
 * 不能操作的流程管理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateProcessManager implements ProcessManager{

    private final ProcessManager processManager;

    public DoNotOperateProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }


    @Override
    public void start() throws Exception {
        throw new Exception("Do not operate start");
    }

    @Override
    public void stop() throws Exception {
        throw new Exception("Do not operate stop");
    }

    @Override
    public String getProcessId() {
        return processManager.getProcessId();
    }

    @Override
    public ProcessConfig getProcessConfig() {
        return processManager.getProcessConfig();
    }

    @Override
    public State state() {
        return processManager.state();
    }

    @Override
    public Input getInput() {
        return processManager.getInput();
    }

    @Override
    public DateHandler getDateHandler() {
        return processManager.getDateHandler();
    }

    @Override
    public Output getOutput() {
        return processManager.getOutput();
    }
}

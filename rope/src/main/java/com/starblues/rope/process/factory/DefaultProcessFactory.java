package com.starblues.rope.process.factory;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.google.common.collect.Maps;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.common.manager.Manager;
import com.starblues.rope.core.handler.DateHandlerFactory;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.InputFactory;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.input.manager.InputManagerFactory;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.output.OutputFactory;
import com.starblues.rope.core.output.OutputManagerConfig;
import com.starblues.rope.core.output.manager.OutputManagerFactory;
import com.starblues.rope.core.transport.DoNotOperateTransport;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.core.transport.factory.InputTransportFactory;
import com.starblues.rope.core.transport.factory.OutputTransportFactory;
import com.starblues.rope.core.transport.factory.TransportFactory;
import com.starblues.rope.process.DefaultProcessManager;
import com.starblues.rope.process.DoNotOperateProcessManager;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.process.config.Configuration;
import com.starblues.rope.core.common.config.ProcessConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 默认的流程工厂。系统核心
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class DefaultProcessFactory implements ProcessFactory{

    private final StateControl stateControl = new StateControl();

    /**
     * 读写锁
     */

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * 运行中的流程缓存
     */
    private final Map<String, ProcessManager> runProcessManagers = Maps.newHashMap();

    /**
     * 停止的流程缓存
     */
    private final Map<String, ProcessManager> stopProcessManagers = Maps.newHashMap();

    private final PluginApplication pluginApplication;

    private PluginUser pluginUser;
    private InputFactory inputFactory;
    private OutputFactory outputFactory;
    private DateHandlerFactory dateHandlerFactory;

    /**
     * 输入管理者
     */
    private Manager<Input> inputManager;
    /**
     * 输出管理器
     */
    private Manager<Output> outputManager;

    private final TransportFactory inputTransportFactory;
    private final TransportFactory outputTransportFactory;
    private Transport inputTransport;
    private Transport outputTransport;



    public DefaultProcessFactory(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
        this.inputTransportFactory = new InputTransportFactory(this);
        this.outputTransportFactory = new OutputTransportFactory(this);
    }


    @Override
    public void start(Configuration configuration) throws Exception {
        checkConfiguration(configuration);
        stateControl.start();
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            this.pluginUser = pluginApplication.getPluginUser();
            // 启动输入的 Transport
            this.inputTransport = inputTransportFactory.getTransport(configuration.getInputTransport());
            this.inputTransport.start();
            // 启动输出的 Transport
            this.outputTransport = outputTransportFactory.getTransport(configuration.getOutputTransport());
            this.outputTransport.start();
            // 初始化输入管理器
            this.inputManager = initializeInputManager(configuration.getInputConfig());
            // 初始化输出管理器
            this.outputManager = initializeOutputManager(configuration.getOutputConfig());
            // 初始化工厂
            initializeFactory(configuration);
            log.info("ProcessFactory start success", configuration.getName());
            stateControl.startSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            log.info("ProcessFactory start failure", configuration.getName());
            throw e;
        } finally {
            lock.unlock();
        }
    }



    /**
     * 启动之前检查配置
     * @param configuration 配置
     */
    private void checkConfiguration(Configuration configuration) {
        if(configuration == null){
            throw new IllegalArgumentException("Start failure. ConfigParamInfo can't be null");
        }
        Objects.requireNonNull(configuration.getInputConfig(),
                "Start failure. ConfigParamInfo.inputConfig can't be null");

        Objects.requireNonNull(configuration.getInputTransport(),
                "Start failure. ConfigParamInfo.inputTransport can't be null");

        Objects.requireNonNull(configuration.getOutputTransport(),
                "Start failure. ConfigParamInfo.outputTransport can't be null");
    }


    /**
     * 初始化输入管理器
     * @param config 输入管理器配置
     * @return  Manager<Input>
     */
    protected Manager<Input> initializeInputManager(InputManagerConfig config){
        InputManagerFactory inputManager = new InputManagerFactory(config, getInputTransport());
        inputManager.initialize();
        return inputManager;
    }


    /**
     * 初始化输出管理器
     * @param config 输出管理器配置
     * @return  Manager<Output>
     */
    protected Manager<Output> initializeOutputManager(OutputManagerConfig config){
        OutputManagerFactory outputManager = new OutputManagerFactory(config);
        outputManager.initialize();
        return outputManager;
    }


    /**
     * 初始化工厂
     * @param configuration 配置bean
     */
    private void initializeFactory(Configuration configuration) {
        this.inputFactory = new InputFactory(pluginUser);
        this.outputFactory = new OutputFactory(configuration.getOutputPollConfig(), pluginUser);
        this.dateHandlerFactory = new DateHandlerFactory(pluginUser);
    }


    @Override
    public void stop() throws Exception {
        stateControl.stop();
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            boolean isSuccess = true;
            runProcessManagers.forEach((processId, processManager) -> {
                try {
                    processManager.stop();
                    stopProcessManagers.put(processId, processManager);
                    log.info("Stop process '{}' success", processId);
                } catch (Exception e) {
                    log.error("Stop process '{}' failure. {}", processId, e.getMessage(), e);
                }
            });
            runProcessManagers.clear();
            try {
                inputTransport.stop();
            } catch (Exception e){
                log.error("Stop input transport '{}' failure. {}", inputTransport.id(), e.getMessage(), e);
                isSuccess = false;
            }
            try {
                outputTransport.stop();
            }catch (Exception e){
                log.error("Stop output transport '{}' failure. {}", outputTransport.id(), e.getMessage(), e);
                isSuccess = false;
            }
            if(isSuccess){
                stateControl.stopSuccessful();
                log.info("Stop process factory success");
            } else {
                stateControl.throwable(new Exception("Stop process factory failure"));
            }
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public State state() {
        return stateControl.getCurrentState();
    }

    @Override
    public Transport getInputTransport() {
        return new DoNotOperateTransport(inputTransport);
    }

    @Override
    public Transport getOutputTransport() {
        return new DoNotOperateTransport(outputTransport);
    }

    @Override
    public ProcessManager create(ProcessConfig processConfig) throws Exception{
        if(processConfig == null){
            return null;
        }
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            check(processConfig);
            ProcessManager processManager = new DefaultProcessManager(processConfig,
                    inputFactory, outputFactory, dateHandlerFactory,
                    inputManager, outputManager);
            stopProcessManagers.put(processConfig.getProcessId(), processManager);
            log.info("Create process '{}' success", processConfig.getProcessId());
            return processManager;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ProcessManager start(String processId) throws Exception {
        if(StringUtils.isEmpty(processId)){
            return null;
        }
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            if(runProcessManagers.containsKey(processId)){
                throw new Exception("The process '" + processId + "' has been started");
            }
            ProcessManager processManager = stopProcessManagers.get(processId);
            if(processManager == null){
                throw new Exception("No running process " + processId + " was found. Please create the process");
            }
            processManager.start();
            stopProcessManagers.remove(processId);
            runProcessManagers.put(processId, processManager);
            log.info("Start process '{}' success", processId);
            return processManager;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void stop(String processId) throws Exception {
        if(StringUtils.isEmpty(processId)){
            return;
        }
        check();
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            ProcessManager processManager = runProcessManagers.get(processId);
            if(processManager == null){
                throw new Exception("No running process " + processId + " was found");
            }
            processManager.stop();
            runProcessManagers.remove(processId);
            stopProcessManagers.put(processId, processManager);
            log.info("Stop process '{}' success", processId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(String processId) throws Exception {
        if(StringUtils.isEmpty(processId)){
            return;
        }
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            ProcessManager processManager = runProcessManagers.get(processId);
            if(processManager != null){
                stop(processId);
            }
            stopProcessManagers.remove(processId);
            log.info("Remove process '{}' success", processId);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public ProcessManager getProcessManager(String processId) {
        if(StringUtils.isEmpty(processId)){
            return null;
        }
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            ProcessManager processManager = runProcessManagers.get(processId);
            if(processManager != null){
                return processManager;
            }
            return stopProcessManagers.get(processId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean exist(String processId) {
        if(StringUtils.isEmpty(processId)){
            return false;
        }
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            if(runProcessManagers.containsKey(processId) ||
                    stopProcessManagers.containsKey(processId)){
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ProcessManager> getRunProcessManager() {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            return runProcessManagers.values().stream()
                    .filter(processManager -> processManager != null)
                    .map(processManager -> getDoNotOperateProcessManager(processManager))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ProcessManager> getStopProcessManager() {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            List<ProcessManager> stopProcessManagers = this.stopProcessManagers.values().stream()
                    .filter(processManager -> processManager != null)
                    .map(processManager -> getDoNotOperateProcessManager(processManager))
                    .collect(Collectors.toList());
            return stopProcessManagers;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 得到不能操作的流程管理器
     * @param processManager 流程管理器
     * @return 不可操作的流程管理器
     */
    private ProcessManager getDoNotOperateProcessManager(ProcessManager processManager){
        if(processManager == null){
            return null;
        }
        return new DoNotOperateProcessManager(processManager);
    }



    /**
     * 操作检查
     * @throws Exception 检查一次
     */
    private void check() throws Exception {
        if(state() != State.RUNNING){
            throw new RuntimeException("Unable to operate. The process factory did not start");
        }
    }

    /**
     * 操作检查
     * @throws Exception 检查一次
     */
    private void check(ProcessConfig processConfig) throws Exception {
        check();
        if(processConfig == null){
            throw new IllegalArgumentException("ProcessConfig can't be null");
        }
        String processId = processConfig.getProcessId();
        if(StringUtils.isEmpty(processId)){
            throw new IllegalArgumentException("ProcessConfig.processId can't be null");
        }
        if(runProcessManagers.containsKey(processId) || stopProcessManagers.containsKey(processId)){
            throw new RuntimeException("The process '" + processId + "' already exists");
        }
        ProcessConfig.InputConfig input = processConfig.getInput();
        if(input == null){
            throw new IllegalArgumentException("Input config can't be null");
        }
        check(input,"Input");

        ProcessConfig.OutputConfig outputConfig = processConfig.getOutput();
        if(outputConfig == null){
            throw new IllegalArgumentException("Output config can't be null");
        }
        check(outputConfig,"Output");
    }



    private void check(CommonConfig commonConfig, String type){
        if(commonConfig == null){
            throw new IllegalArgumentException(type + " config can't be null");
        }
        if(StringUtils.isEmpty(commonConfig.getId())){
            throw new IllegalArgumentException(type + " id config can't be null");
        }
    }


}

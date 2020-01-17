package com.starblues.rope.core.input.manager;

import com.starblues.rope.core.common.manager.AbstractManagerFactory;
import com.starblues.rope.core.common.manager.Manager;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.input.manager.support.AcceptInputManager;
import com.starblues.rope.core.input.manager.support.OneInputManager;
import com.starblues.rope.core.input.manager.support.PeriodAcquireInputManager;
import com.starblues.rope.core.input.manager.support.QuartzInputManager;
import com.starblues.rope.core.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * 输入管理者工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class InputManagerFactory extends AbstractManagerFactory<Input> {

    private final InputManagerConfig configuration;
    private final Transport inputTransport;


    public InputManagerFactory(InputManagerConfig inputConfig, Transport inputTransport){
        this.configuration = inputConfig;
        this.inputTransport = inputTransport;
    }


    @Override
    public void initialize() {
        addManager(new AcceptInputManager(inputTransport));
        addManager(getOneInputManager());
        addManager(getPeriodAcquireInputManager());
        try {
            addManager(getQuartzInputManager());
        } catch (SchedulerException e) {
            log.error("Initialize quartz schedulerFactory failure. {}", e.getMessage(), e);
        }
    }

    @Override
    protected String name() {
        return "input";
    }


    private Manager getOneInputManager(){
        return new OneInputManager(inputTransport);
    }

    private Manager getPeriodAcquireInputManager(){
        InputManagerConfig.PeriodAcquireInput periodAcquire = configuration.getPeriodAcquire();
        return new PeriodAcquireInputManager(inputTransport,
                periodAcquire.getCorePoolSize(),
                periodAcquire.getThreadFactory());
    }

    private Manager getQuartzInputManager() throws SchedulerException {
        Properties properties = configuration.getQuartz();
        SchedulerFactory stdSchedulerFactory = null;

        if(properties != null){
            stdSchedulerFactory = new StdSchedulerFactory(properties);
        } else {
            stdSchedulerFactory = new StdSchedulerFactory();
        }

        return new QuartzInputManager(inputTransport, stdSchedulerFactory);
    }


}

package com.starblues.rope.core.input.manager;

import com.starblues.rope.core.common.manager.AbstractManagerFactory;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.input.manager.support.AcceptInputManager;
import com.starblues.rope.core.input.manager.support.OneInputManager;
import com.starblues.rope.core.input.manager.support.PeriodAcquireInputManager;
import com.starblues.rope.core.input.manager.support.QuartzInputManager;
import com.starblues.rope.core.transport.Transport;
import lombok.extern.slf4j.Slf4j;

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
        // 得到一次性执行的输入管理者
        addManager(new OneInputManager(inputTransport, configuration.getOneInput()));
        // 得到Java周期性执行的输入管理者
        addManager(new PeriodAcquireInputManager(inputTransport, configuration.getPeriodAcquire()));
        // 得到Quartz周期性执行的输入管理者
        addManager(new QuartzInputManager(inputTransport, configuration.getQuartzProp()));
    }

    @Override
    protected String name() {
        return "input";
    }



}

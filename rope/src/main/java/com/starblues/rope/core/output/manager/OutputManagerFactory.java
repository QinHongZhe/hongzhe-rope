package com.starblues.rope.core.output.manager;

import com.starblues.rope.core.common.manager.AbstractManagerFactory;
import com.starblues.rope.core.common.manager.Manager;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.output.OutputManagerConfig;
import com.starblues.rope.core.output.manager.support.BatchOutputManager;
import com.starblues.rope.core.output.manager.support.BatchTimeIntervalOutputManager;
import com.starblues.rope.core.output.manager.support.SimpleOutputManager;
import com.starblues.rope.core.output.manager.support.TimeIntervalOutputManager;

import java.util.Objects;

/**
 * 输出管理器工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class OutputManagerFactory extends AbstractManagerFactory<Output> {


    private final OutputManagerConfig configuration;

    public OutputManagerFactory(OutputManagerConfig configuration) {
        this.configuration = Objects.requireNonNull(configuration, "OutputManagerConfig can't be null");
    }


    @Override
    public void initialize() {
        addManager(new SimpleOutputManager());
        addManager(new BatchOutputManager());
        addManager(getTimeIntervalOutputManager());
        addManager(getBatchTimeIntervalOutputManager());
    }

    /**
     * 得到 TimeIntervalOutputManager
     * @return Manager
     */
    private Manager getTimeIntervalOutputManager(){
        OutputManagerConfig.TimeIntervalOutput timeInterval = configuration.getTimeInterval();
        return new TimeIntervalOutputManager(
                timeInterval.getCorePoolSize(),
                timeInterval.getThreadFactory());
    }


    /**
     * 得到 BatchTimeIntervalOutputManager
     * @return Manager
     */
    private Manager getBatchTimeIntervalOutputManager(){
        OutputManagerConfig.TimeIntervalOutput timeInterval = configuration.getTimeInterval();
        return new BatchTimeIntervalOutputManager(
                timeInterval.getCorePoolSize(),
                timeInterval.getThreadFactory());
    }


    @Override
    protected String name() {
        return "output";
    }
}

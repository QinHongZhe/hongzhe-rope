package com.starblues.rope.core.output.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.output.support.SimpleOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 简单的输出工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class SimpleOutputManager extends AbstractManager<SimpleOutput> {

    public static final String ID = "simple";

    public SimpleOutputManager() {
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String name() {
        return "SimpleOutput";
    }

    @Override
    protected void toStart(SimpleOutput managed) throws Exception {
        managed.start();
    }

    @Override
    protected void toStop(SimpleOutput managed) throws Exception {
        managed.stop();
    }
}

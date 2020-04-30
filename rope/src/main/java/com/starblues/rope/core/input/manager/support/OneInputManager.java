package com.starblues.rope.core.input.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.input.support.reader.OneReaderInput;
import com.starblues.rope.core.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 一次性输入管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class OneInputManager extends AbstractManager<OneReaderInput> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Transport transport;
    private final ExecutorService executor;

    public OneInputManager(Transport transport) {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
        executor = Executors.newCachedThreadPool();
    }


    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected String name() {
        return "OneInputManager";
    }

    @Override
    protected void toStart(OneReaderInput managed) throws Exception {
        executor.execute(()->{
            try {
                managed.start(transport);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void toStop(OneReaderInput managed) throws Exception {
        managed.stop();
    }
}

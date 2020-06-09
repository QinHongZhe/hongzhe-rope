package com.starblues.rope.core.input.manager.support;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.input.support.reader.OneReaderInput;
import com.starblues.rope.core.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一次性输入管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class OneInputManager extends AbstractManager<OneReaderInput> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Transport transport;
    private final InputManagerConfig.OneInput oneInput;

    private volatile ExecutorService executor;

    public OneInputManager(Transport transport, InputManagerConfig.OneInput oneInput) {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
        this.oneInput = Objects.requireNonNull(oneInput, "oneInput config can't be null");
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
    protected synchronized void toStart(OneReaderInput managed) throws Exception {
        checkAndInitialize();
        executor.execute(()->{
            try {
                managed.start(transport);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkAndInitialize() {
        if(executor == null){
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("OneInput-Pool-%d")
                    .build();
            int threadNumber = oneInput.getThreadNumber();
            if(threadNumber <= 0){
                threadNumber = InputManagerConfig.OneInput.DEFAULT_THREAD_NUMBER;
            }
            executor = new ThreadPoolExecutor(threadNumber, threadNumber,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadNumber),
                    threadFactory,  new ThreadPoolExecutor.AbortPolicy());
        }
    }

    @Override
    protected synchronized void toStop(OneReaderInput managed) throws Exception {
        managed.stop();
    }

}

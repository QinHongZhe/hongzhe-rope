package com.starblues.rope.core.input.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.input.support.accept.AbstractAcceptInput;
import com.starblues.rope.core.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 接受型输入管理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class AcceptInputManager extends AbstractManager<AbstractAcceptInput> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Transport transport;

    public AcceptInputManager(Transport transport) {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
    }

    @Override
    public boolean support(Object object) {
        return object instanceof AbstractAcceptInput;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected String name() {
        return "AcceptInputManager";
    }

    @Override
    protected void toStart(AbstractAcceptInput managed) throws Exception {
        managed.start(transport);
    }

    @Override
    protected void toStop(AbstractAcceptInput managed) throws Exception {
        managed.stop();
    }

}

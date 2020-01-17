package com.starblues.rope.core.output.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.output.support.BatchOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 批量输出
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class BatchOutputManager extends AbstractManager<BatchOutput> {


    @Override
    protected String name() {
        return "BatchOutput";
    }

    @Override
    protected void toStart(BatchOutput managed) throws Exception {
        managed.start();
    }

    @Override
    protected void toStop(BatchOutput managed) throws Exception {
        managed.stop();
    }


    @Override
    public Logger getLogger() {
        return log;
    }
}

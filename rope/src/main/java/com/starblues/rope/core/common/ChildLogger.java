package com.starblues.rope.core.common;

import org.slf4j.Logger;

/**
 * 获取子类的日志打印对象的接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ChildLogger {

    /**
     * 返回日志打印对象
     * @return Logger
     */
    Logger getLogger();

}

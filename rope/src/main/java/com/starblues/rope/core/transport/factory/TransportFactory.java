package com.starblues.rope.core.transport.factory;

import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.core.common.config.CommonConfig;

/**
 * 数据传输器工厂接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface TransportFactory {

    /**
     * 得到数据传输器
     * @param commonConfig 传输器配置
     * @return Transport
     * @throws Exception 得到Transport时的异常
     */
    Transport getTransport(CommonConfig commonConfig) throws Exception;



}

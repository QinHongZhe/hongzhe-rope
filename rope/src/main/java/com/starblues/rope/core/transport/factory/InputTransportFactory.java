package com.starblues.rope.core.transport.factory;

import com.starblues.rope.config.StaticBean;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.transport.buffer.AbstractBufferTransport;
import com.starblues.rope.core.transport.input.BufferInputTransport;
import com.starblues.rope.core.transport.input.DefaultInputTransport;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.utils.MetricUtils;
import org.springframework.util.StringUtils;

import java.util.Map;


/**
 * 输入数据传输器工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class InputTransportFactory implements TransportFactory{


    private final ProcessFactory processFactory;

    public InputTransportFactory(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }


    @Override
    public Transport getTransport(CommonConfig config) throws Exception {
        if(config == null){
            throw new NullPointerException("Get input transport failure, inputTransport config is null");
        }
        String id = config.getId();
        if(StringUtils.isEmpty(id)){
            throw new NullPointerException("Get input transport failure, inputTransport config [id] is empty");
        }
        Transport transport = null;
        switch (id){
            case DefaultInputTransport.ID :{
                transport = new DefaultInputTransport(processFactory);
                break;
            }
            case AbstractBufferTransport.ID :{
                transport = getBufferTransport(processFactory, config.getParams());
                break;
            }
            default:
                throw new Exception("The '" + id + "' input transporter no was found in the system");
        }

        MetricUtils.safelyRegister(StaticBean.metricRegistry, transport.getMetricSet());
        return transport;
    }


    private Transport getBufferTransport(ProcessFactory processFactory,
                                 Map<String, Object> params) throws Exception {
        AbstractBufferTransport.Param param = new AbstractBufferTransport.Param();
        ConfigParamInfo configParamInfo = new ConfigParamInfo(params);
        param.parsing(configParamInfo);
        return new BufferInputTransport(processFactory, param);
    }



}

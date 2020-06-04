package com.starblues.rope.core.handler;

import com.gitee.starblues.integration.user.PluginUser;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据处理器工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class DateHandlerFactory {

    private final PluginUser pluginUser;


    public DateHandlerFactory(PluginUser pluginUser) {
        this.pluginUser = pluginUser;
    }

    /**
     * 得到数据处理流
     * @param dateHandlerFlow 数据处理者的配置集合
     * @return 数据处理者
     */
    public DateHandler getDateHandler(String processId, List<CommonConfig> dateHandlerFlow){
        if(dateHandlerFlow == null || dateHandlerFlow.isEmpty()){
            return new DateHandlerFlow(null);
        }
        List<DateHandler> dateHandlers = new ArrayList<>();
        for (CommonConfig dateHandlerConfig : dateHandlerFlow) {
            DateHandler dateHandler = CommonUtils.getImpls(pluginUser, DateHandler.class, impl -> {
                return Objects.equals(impl.id(), dateHandlerConfig.getId());
            });
            if(dateHandler == null){
                continue;
            }
            try {
                // 生成一个全新的对象。则多个流程会公用同一个数据转换器。会导致数据错误。
                dateHandler = pluginUser.generateNewInstance(dateHandler);
                ConfigParameter configParameter = dateHandler.configParameter();
                CommonUtils.parsingConfig(configParameter, dateHandlerConfig.getParams());
                if(dateHandler.initialize(processId)){
                    dateHandlers.add(dateHandler);
                }
            } catch (Exception e) {
                log.error("数据处理者 '{}' 初始化失败. {}", dateHandler.id(), e.getMessage(), e);
            }
        }
        return new DateHandlerFlow(dateHandlers);
    }


}

package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.TimeUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 设置时间处理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SetTimeDateHandler implements DateHandler {

    public static final String ID = "set-time";

    private final Param param;

    public SetTimeDateHandler() {
        this.param = new Param();
    }


    @Override
    public void initialize(String processId) throws Exception {

    }

    @Override
    public Record handle(Record record) throws Exception{
        String format = param.getFormat();
        String time = null;
        if(StringUtils.isEmpty(format)){
            time = TimeUtil.getNowTimeToMs();
        } else {
            time = TimeUtil.getNowTimeByFormat(format);
        }
        record.putColumn(Column.auto(param.getKey(), time));
        return record;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public ConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "设置指定格式的时间";
    }

    @Override
    public String describe() {
        return "设置指定格式的时间";
    }


    @Getter
    public static class Param implements ConfigParameter{

        private static final String P_KEY = "key";
        private static final String P_FORMAT = "format";

        private String key;
        private String format;

        @Override
        public void parsing(ConfigParamInfo configParamInfo) {
            this.key = configParamInfo.getString(P_KEY);
            this.format = configParamInfo.getString(P_FORMAT);
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();
            configParam.addField(
                    TextField.toBuilder(
                            P_KEY, "key", "time")
                            .description("时间字段的key字段值")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            P_FORMAT, "格式化", "yyyy-MM-dd HH:mm:ss.SSS")
                            .description("时间的格式")
                            .required(true)
                            .build()
            );

            return configParam;
        }
    }

}

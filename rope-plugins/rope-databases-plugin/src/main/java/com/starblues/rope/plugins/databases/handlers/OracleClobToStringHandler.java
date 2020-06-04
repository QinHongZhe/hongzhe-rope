package com.starblues.rope.plugins.databases.handlers;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.Record;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Set;

/**
 * oracle clob 类型转换String处理器
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-19
 */
@Component
@Slf4j
public class OracleClobToStringHandler implements DateHandler {

    private final static String ID = "oracle-clob-to-string";

    private final Param param = new Param();


    @Override
    public boolean initialize(String processId) throws Exception {
        return true;
    }

    @Override
    public Record handle(Record record) throws Exception {
        Set<String> clobKeys = param.getClobKeySet();
        if(clobKeys == null || clobKeys.isEmpty()){
            return record;
        }
        for (String clobKey : clobKeys) {
            if(StringUtils.isEmpty(clobKey)){
                continue;
            }
            Column column = record.getColumn(clobKey);
            if(column == null){
                return record;
            }
            Object metadata = column.getMetadata();
            if(metadata instanceof Clob){
                Clob clob = (Clob) metadata;
                try {
                    metadata = clob.getSubString(1, (int) clob.length());
                    column.setMetadata(metadata, true);
                } catch (SQLException e) {
                    log.error("Clob Type conversion failed!", e);
                }
            }
        }
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
        return "Clob转换String";
    }

    @Override
    public String describe() {
        return "oracle 'Clob类型’ 转换 ‘String类型’ 处理器";
    }

    @Getter
    public static class Param implements ConfigParameter {

        private final static String P_CLOB_KEY = "clobKeys";

        private String clobKeys;

        private Set<String> clobKeySet;

        @Override
        public void parsing(ConfigParamInfo paramInfo) throws Exception {
            clobKeys = paramInfo.getString(P_CLOB_KEY, "");

            if(!StringUtils.isEmpty(clobKeys)){
                clobKeySet = Sets.newHashSet(
                        Splitter.on(",")
                                .trimResults()
                                .omitEmptyStrings()
                                .split(clobKeys)

                );
            }





        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    TextField.toBuilder(P_CLOB_KEY, "clob字段的key", "")
                            .required(true)
                            .description("记录中clob字段的key, 多个逗号分隔")
                            .build()
            );

            return configParam;
        }
    }

}

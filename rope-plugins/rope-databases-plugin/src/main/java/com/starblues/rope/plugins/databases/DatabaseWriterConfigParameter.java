package com.starblues.rope.plugins.databases;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;

/**
 * 数据库写入者的参数配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DatabaseWriterConfigParameter extends BaseWriterConfigParameter {

    private final DatabaseConfigParameter databaseConfigParameter;

    public DatabaseWriterConfigParameter(DatabasesConfig databasesConfig) {
        this.databaseConfigParameter = new DatabaseConfigParameter(databasesConfig);
    }


    @Override
    protected void childParsing(ConfigParamInfo configParamInfo) {
        databaseConfigParameter.parsing(configParamInfo);
    }

    @Override
    protected void configParam(ConfigParam configParam) {
        configParam.copyFields(databaseConfigParameter.configParam());
    }

    public String getDatabaseKey() {
        return databaseConfigParameter.getDatabaseKey();
    }

}

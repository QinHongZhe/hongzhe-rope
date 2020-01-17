package com.starblues.rope.plugins.databases;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.input.reader.BaseReaderConfigParameter;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;

/**
 * 数据库读取者的参数配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DatabaseReaderConfigParameter extends BaseReaderConfigParameter {

    private final DatabaseConfigParameter databaseConfigParameter;

    public DatabaseReaderConfigParameter(DatabasesConfig databasesConfig) {
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

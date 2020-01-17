package com.starblues.rope.plugins.databases;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.plugins.databases.config.DatabaseConfig;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;
import com.google.common.collect.Maps;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DatabaseConfigParameter implements ConfigParameter {

    public static final String DATABASE_KEY = "databaseKey";
    private final DatabasesConfig databasesConfig;

    private String databaseKey;

    public DatabaseConfigParameter(DatabasesConfig databasesConfig) {
        this.databasesConfig = databasesConfig;
    }


    @Override
    public void parsing(ConfigParamInfo configParamInfo) {
        this.databaseKey = configParamInfo.getString(DATABASE_KEY);
    }

    @Override
    public ConfigParam configParam() {
        ConfigParam configParam = new ConfigParam();

        List<DatabaseConfig> databases = databasesConfig.getDatabases();
        String defaultValue = "";
        Map<String, String> values = Maps.newHashMapWithExpectedSize(databases.size());
        for (DatabaseConfig database : databases) {
            if(StringUtils.isEmpty(defaultValue)){
                defaultValue = database.getKey();
            }
            values.put(database.getKey(), database.getName());
        }

        DropdownField dropdownField = DropdownField.toBuilder(
                DATABASE_KEY, "数据连接key", defaultValue, values)
                .required(true)
                .description("当前配置的数据库连接key")
                .build();
        configParam.addField(dropdownField);
        return configParam;
    }


    public String getDatabaseKey() {
        return databaseKey;
    }
}

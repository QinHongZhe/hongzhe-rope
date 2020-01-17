package com.starblues.rope.plugins.databases.reader;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.reader.Consumer;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.plugins.databases.DatabaseReaderConfigParameter;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;
import com.starblues.rope.plugins.databases.config.DatabasesConfigBean;
import com.starblues.rope.utils.ParamUtils;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据库简单方式读取者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class DatabaseSimpleReader implements Reader {

    private static final String KEY = "database-simple-reader";

    private final DatabasesConfigBean databasesConfigBean;

    private final Param param;

    private Jdbi jdbi;

    public DatabaseSimpleReader(DatabasesConfig databasesConfig,
                                DatabasesConfigBean databasesConfigBean) {
        this.databasesConfigBean = databasesConfigBean;
        this.param = new Param(databasesConfig);
    }

    @Override
    public void initialize(String processId) throws Exception {
        this.jdbi = databasesConfigBean.getJdbi(param.getDatabaseKey());
    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        ParamUtils.check("querySql", param.getQuerySql());
        List<Map<String, Object>> listMap = jdbi.withHandle(handle -> {
            return handle.select(param.getQuerySql())
                    .mapToMap()
                    .list();
        });
        if(listMap != null && !listMap.isEmpty()){
            RecordGroup recordGroup = new RecordGroup();
            for (Map<String, Object> map : listMap) {
                if(map == null || map.isEmpty()){
                    continue;
                }
                Record record = DefaultRecord.instance();
                map.forEach((k,v)->{
                    record.putColumn(Column.auto(k, v));
                });
                recordGroup.addRecord(record);
            }
            consumer.accept(recordGroup);
        }
    }

    @Override
    public void destroy() throws Exception {
        databasesConfigBean.destroy(param.getDatabaseKey());
    }

    @Override
    public DatabaseReaderConfigParameter configParameter() {
        return param;
    }


    @Override
    public String id() {
        return KEY;
    }

    @Override
    public String name() {
        return "数据库简单读取者";
    }

    @Override
    public String describe() {
        return "用于从数据库中简单读取数据";
    }

    @Getter
    public class Param extends DatabaseReaderConfigParameter{

        private static final String QUERY_SQL = "querySql";
        private String querySql;

        Param(DatabasesConfig databasesConfig) {
            super(databasesConfig);
        }


        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            super.childParsing(configParamInfo);
            this.querySql = configParamInfo.getString(QUERY_SQL);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);
            TextField textField = TextField.toBuilder(
                    QUERY_SQL, "查询Sql", "")
                    .attribute(TextField.Attribute.TEXTAREA)
                    .description("从数据库查询数据的Sql. 尽可能不要使用 * 查询")
                    .required(true)
                    .build();
            configParam.addField(textField);
        }

    }

}

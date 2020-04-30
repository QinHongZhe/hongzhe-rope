package com.starblues.rope.plugins.databases.writer;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.ListMapField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.writer.AbstractWriter;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.plugins.databases.DatabaseWriterConfigParameter;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;
import com.starblues.rope.plugins.databases.config.DatabasesConfigBean;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starblues.rope.utils.Converter;
import com.starblues.rope.utils.IDUtils;
import com.starblues.rope.utils.TextUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数据库简单写入者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class DatabaseSimpleWriter extends AbstractWriter {

    private final static String ID = "database-simple-writer";

    private final DatabasesConfigBean databasesConfigBean;

    private final Param param;
    private Jdbi jdbi;
    private String preSql;
    private boolean preResult = false;

    protected DatabaseSimpleWriter(DatabasesConfigBean databasesConfigBean,
                                   DatabasesConfig databasesConfig) {
        this.databasesConfigBean = Objects.requireNonNull(databasesConfigBean,
                "DatabasesConfigBean can't be null");
        this.param = new Param(databasesConfig);
    }


    @Override
    public void initialize(String processId) throws Exception {
        this.jdbi = databasesConfigBean.getJdbi(param.getDatabaseKey());

        jdbi.useHandle(handle -> {
            String tableExistSql = param.getTableExistSql();
            String createTableSql = param.getCreateTableSql();
            String result = null;
            try {
                result = handle.select(tableExistSql)
                        .mapTo(String.class)
                        .one();
            }catch (Exception e) {
                result = null;
                log.warn("TableExistSql:{} is error Or table:{} not exist!",tableExistSql,param.getTableName());
            }finally {
                if(!Strings.isNullOrEmpty(result)){
                    return;
                }
                try {
                    // 创建表sql
                    handle.execute(createTableSql);
                }catch (Exception e) {
                    log.warn("CreateTableSql:{} is error Or table:{} is exist!",createTableSql,param.getTableName());
                    e.printStackTrace();
                }

            }
        });

        // 生成预sql
        List<String> filed = Lists.newArrayList();
        List<String> value = Lists.newArrayList();
        Map<String, String> fieldMappings = param.getFieldMappings();
        if(fieldMappings != null && !fieldMappings.isEmpty()){
            fieldMappings
                    .forEach((mapping, tableFiled)->{
                        if(Objects.equals(tableFiled, param.getId())
                                && Objects.equals(param.getIdType(), Param.ID_TYPE_AUTO)){
                            // 自增字段不加入
                            return;
                        }
                        filed.add(tableFiled);
                        value.add(":" + tableFiled);
                    });
            this.preSql = TextUtils.format("insert into {} ({}) values ({})",
                    param.getTableName(),
                    Joiner.on(",").join(filed),
                    Joiner.on(",").join(value)
            );
            preResult = true;
        } else {
            log.warn("ProcessInfo {} database writer {} fieldMappings is empty. Cannot write data",
                    processId, id());
            preResult = false;
        }
    }

    @Override
    public void write(List<Record> records) throws Exception {
        if(!preResult){
            return;
        }
        cleanData();
        try (Handle handle = jdbi.open()) {
            PreparedBatch preparedBatch = handle.prepareBatch(preSql);
            for (Record record : records) {
                if(record == null){
                    continue;
                }
                Map<String, Object> recordMap  = record.toMap();
                if(recordMap.isEmpty()){
                    continue;
                }
                Map<String, Object> bindMap = getBindMap(record);
                if(bindMap != null && !bindMap.isEmpty()){
                    preparedBatch = preparedBatch.bindMap(bindMap).add();
                }
            }
            preparedBatch.execute();
        }
    }


    /**
     * 得到绑定值得map集合
     * @param record 记录
     * @return map集合
     */
    private Map<String, Object> getBindMap(Record record){
        if(record == null){
            return null;
        }
        Map<String, Object> recordMap  = record.toMap();
        Map<String, String> properties = param.getFieldMappings();
        Map<String, Object> bindMap = Maps.newHashMap();
        properties.forEach((mappingFiled, tableFiled)->{
            if(!recordMap.containsKey(mappingFiled)){
                // 记录中不存在该表字段
                bindMap.put(tableFiled, null);
                return;
            }
            if(Objects.equals(tableFiled, param.getId())){
                // 是id字段
                String idType = param.getIdType();
                if(StringUtils.isEmpty(idType)){
                    bindMap.put(tableFiled, recordMap.get(mappingFiled));
                    return;
                }
                switch (idType){
                    case Param.ID_TYPE_UUID:
                        bindMap.put(tableFiled, IDUtils.uuid());
                        break;
                    case Param.ID_TYPE_FOLLOW:
                        bindMap.put(tableFiled, recordMap.get(mappingFiled));
                    case Param.ID_TYPE_AUTO:
                        // 自增字段类型的值不加入
                        break;
                    default:
                        // 其他id字段类型。跟随
                        bindMap.put(tableFiled, recordMap.get(mappingFiled));
                        break;
                }
            } else {
                // 非id字段
                bindMap.put(tableFiled, recordMap.get(mappingFiled));
            }
        });
        return bindMap;
    }

    /**
     * 清除数据
     */
    private void cleanData(){
        Boolean clean = param.getClean();
        if(clean == null || !clean){
            return;
        }
        String cleanSql = param.getCleanSql();
        if(StringUtils.isEmpty(cleanSql)){
            throw new RuntimeException("Clean sql can't be empty");
        }
        jdbi.useHandle(handle -> {
            handle.createUpdate(cleanSql)
                    .execute();
        });
    }


    @Override
    public void destroy() throws Exception {
        databasesConfigBean.destroy(param.getDatabaseKey());
    }

    @Override
    public BaseWriterConfigParameter configParameter() {
        return param;
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "数据库简单写入者";
    }

    @Override
    public String describe() {
        return "数据库简单写入者";
    }

    @Getter
    public static class Param extends DatabaseWriterConfigParameter {

        public static final String MAP_KEY = "key";
        public static final String MAP_VALUE = "value";


        private static final String TABLE_NAME = "tableName";
        private static final String ID = "id";
        private static final String ID_TYPE = "idType";
        private static final String CREATE_TABLE_SQL = "createTableSql";
        private static final String CLEAN = "clean";
        private static final String CLEAN_SQL = "cleanSql";
        private static final String FIELD_MAPPINGS = "fieldMappings";
        private static final String TABLE_EXIST_SQL = "tableExistSql";


        public static final String ID_TYPE_UUID = "uuid";
        public static final String ID_TYPE_AUTO = "auto";
        public static final String ID_TYPE_FOLLOW = "follow";

        private static final Map<String, String> ID_TYPE_OPTIONS = ImmutableMap.of(
                ID_TYPE_UUID, "uuid类型",
                ID_TYPE_AUTO, "自增",
                ID_TYPE_FOLLOW, "跟随写入字段填充");

        /**
         * 表名称
         */
        private String tableName;

        /**
         * 表id字段名称
         */
        private String id;

        /**
         * id 类型.
         */
        private String idType;

        /**
         * 表不存在时, 使用创建表的Sql语句
         */
        private String createTableSql;

        /**
         * 写入数据前是否清除表中的数据
         */
        private Boolean clean;

        /**
         * 删除数据的sql
         */
        private String cleanSql;

        /**
         * 检查表是否存在sql
         */
        private String tableExistSql;

        /**
         * 字段映射。key为写入记录的key, value为表中字段的key
         */
        private Map<String, String> fieldMappings = null;



        public Param(DatabasesConfig databasesConfig) {
            super(databasesConfig);
        }



        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            super.childParsing(configParamInfo);

            this.tableName = configParamInfo.getString(TABLE_NAME);
            this.id = configParamInfo.getString(ID);
            this.idType = configParamInfo.getString(ID_TYPE);
            this.createTableSql = configParamInfo.getString(CREATE_TABLE_SQL);
            this.clean = configParamInfo.getBoolean(CLEAN);
            this.cleanSql = configParamInfo.getString(CLEAN_SQL);
            this.tableExistSql = configParamInfo.getString(TABLE_EXIST_SQL);
            List<Map<String, Object>> listMap = configParamInfo.getListMap(FIELD_MAPPINGS);
            fieldMappings = new HashMap<>();
            if(listMap != null){
                for (Map<String, Object> map : listMap) {
                    Object k = map.get(MAP_KEY);
                    Object v = map.get(MAP_VALUE);
                    if(k != null && v != null){
                        fieldMappings.put(Converter.getAsString(k), Converter.getAsString(v));
                    }
                }
            }
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);

            configParam.addField(
                    TextField.toBuilder(TABLE_NAME, "数据库表名", "")
                            .required(true)
                            .description("数据库表名")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(ID, "id字段", "")
                            .required(true)
                            .description("表中id字段名称")
                            .build()
            );
            configParam.addField(
                    DropdownField.toBuilder(ID_TYPE, "id类型", ID_TYPE_UUID, ID_TYPE_OPTIONS)
                            .required(true)
                            .description("写入表的id字段的类型")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(TABLE_EXIST_SQL, "表存在sql", "")
                            .required(true)
                            .description("用于判断表是否存在的sql")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(CREATE_TABLE_SQL, "创建表sql", "")
                            .attribute(TextField.Attribute.TEXTAREA)
                            .required(true)
                            .description("如果表不存在时, 则使用该处定义的Sql创建表")
                            .build()
            );


            configParam.addField(
                    BooleanField.toBuilder(CLEAN, "是否清除数据", false)
                            .description("在写入数据前, 是否清除数据")
                            .required(true)
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(CLEAN_SQL, "清除数据Sql", "")
                            .description("如果清除数据, 则使用该处清除数据的Sql")
                            .required(false)
                            .build()
            );

            configParam.addField(
                    ListMapField.toBuilder(FIELD_MAPPINGS, "字段映射", MAP_KEY, MAP_VALUE)
                            .description("记录与表的字段映射。key为写入记录的key, value为表中字段的key")
                            .required(false)
                            .build()
            );
        }


    }


}

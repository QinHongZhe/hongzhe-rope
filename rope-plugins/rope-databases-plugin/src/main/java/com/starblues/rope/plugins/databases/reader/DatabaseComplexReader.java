package com.starblues.rope.plugins.databases.reader;

import com.google.common.collect.ImmutableMap;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.reader.BaseReaderConfigParameter;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.plugins.databases.DatabaseReaderConfigParameter;
import com.starblues.rope.plugins.databases.config.DatabasesConfig;
import com.starblues.rope.plugins.databases.config.DatabasesConfigBean;
import com.starblues.rope.utils.ParamUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 复杂的数据库读取者
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-18
 */
@Component
@Slf4j
public class DatabaseComplexReader implements Reader {

    private static final String ID = "database-complex-reader";

    private final Param param;
    private final DatabasesConfigBean databasesConfigBean;

    private Jdbi jdbi;

    public DatabaseComplexReader(DatabasesConfigBean databasesConfigBean,
                                 DatabasesConfig databasesConfig) {
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

        if(param.isPageQuery()){
            // 分页
            // 开始页
            param.setCurrentNum(param.getPageStartNum());

            int pageDelayMs = param.getPageDelayMs();

            while (true){
                List<Map<String, Object>> listMap = jdbi.withHandle(handle -> {
                    Query query = handle.select(param.getQuerySql())
                            .bind(Param.SIGN_START_PAGE, param.getCurrentNum());
                    int endNum;
                    if(Objects.equals(Param.PAGE_TYPE_START_TO_END, param.getPageType())){
                        endNum = param.getCurrentNum() + param.getPageSize();
                    } else {
                        endNum = param.getPageSize();
                    }
                    param.setCurrentNum(param.getCurrentNum() + param.getPageSize());
                    return query.bind(Param.SIGN_ENT_PAGE, endNum)
                            .mapToMap()
                            .list();
                });
                if(listMap == null || listMap.isEmpty()){
                    // 说明已经将数据查询完
                    return;
                }
                consume(listMap, consumer);
                if(pageDelayMs > 0){
                    try {
                        Thread.sleep(pageDelayMs);
                    } catch (Exception e){
                        log.error("Page delay execute failure. {}", e.getMessage());
                    }
                }
            }
        } else {
            // 不分页
            List<Map<String, Object>> listMap = jdbi.withHandle(handle -> {
                return handle.select(param.getQuerySql())
                        .mapToMap()
                        .list();
            });
            consume(listMap, consumer);
        }
    }


    private void consume(List<Map<String, Object>> listMap, Consumer consumer){
        if(listMap != null && !listMap.isEmpty()){
            RecordGroup recordGroup = new RecordGroup();
            recordGroup.addListMap(listMap);
            consumer.accept(recordGroup);
        }
    }


    @Override
    public void destroy() throws Exception {
        databasesConfigBean.destroy(param.getDatabaseKey());
    }

    @Override
    public BaseReaderConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "数据库复杂读取者";
    }

    @Override
    public String describe() {
        return "用于从数据库中读取数据";
    }

    @Getter
    public static class Param extends DatabaseReaderConfigParameter {


        public static final String SIGN_START_PAGE = "page1";
        public static final String SIGN_ENT_PAGE = "page2";


        public static final String PAGE_TYPE_START_TO_SIZE = "startToSize";
        public static final String PAGE_TYPE_START_TO_END = "startToEnd";


        private static final String P_QUERY_SQL = "querySql";
        private static final String P_IS_PAGE_QUERY = "isPageQuery";
        private static final String P_PAGE_TYPE = "pageType";
        private static final String P_PAGE_SIZE = "pageSize";
        private static final String P_PAGE_START_NUM = "pageStartNum";
        private static final String P_PAGE_DELAY_MS = "pageDelayMs";

        private static final Map<String, String> PAGE_TYPES = ImmutableMap.of(
                PAGE_TYPE_START_TO_SIZE, "开始页-页大小",
                PAGE_TYPE_START_TO_END, "开始页-结束页");


        /**
         * 查询sql
         */
        private String querySql;

        /**
         * 如果为分页的话。使用 :page_1 代替开始数字
         * :page_2 代替结束数字/分页大小
         */
        private boolean isPageQuery;

        /**
         * 分页类型, start-to-end, start-to-size
         */
        private String pageType;

        /**
         * 页大小。默认 1000
         */
        private int pageSize = 1000;

        /**
         * 分页初始化起始页大小。默认0
         */
        private int pageStartNum = 0;

        /**
         * 分页查询延迟毫秒数
         */
        private int pageDelayMs = 0;


        public Param(DatabasesConfig databasesConfig) {
            super(databasesConfig);
        }

        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            super.childParsing(configParamInfo);
            this.querySql = configParamInfo.getString(P_QUERY_SQL);
            this.isPageQuery = configParamInfo.getBoolean(P_IS_PAGE_QUERY, false);
            this.pageType = configParamInfo.getString(P_PAGE_TYPE, PAGE_TYPE_START_TO_SIZE);
            this.pageSize = configParamInfo.getInt(P_PAGE_SIZE, pageSize);
            this.pageStartNum = configParamInfo.getInt(P_PAGE_START_NUM, pageStartNum);
            this.pageDelayMs = configParamInfo.getInt(P_PAGE_DELAY_MS, pageDelayMs);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);

            configParam.addField(
                    TextField.toBuilder(
                            P_QUERY_SQL, "查询Sql", "")
                        .attribute(TextField.Attribute.TEXTAREA)
                        .description("从数据库查询数据的Sql. 尽可能不要使用 * 查询, 如果分页查询, 请使用占位符填充分页参数")
                        .required(true)
                        .build()
            );

            configParam.addField(
                    BooleanField.toBuilder(P_IS_PAGE_QUERY, "是否分页查询", false)
                            .description("如果分页查询, 查询sql请使用占位符填充分页参数")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    DropdownField.toBuilder(P_PAGE_TYPE, "分页类型", PAGE_TYPE_START_TO_SIZE, PAGE_TYPES)
                            .required(true)
                            .description("分页类型/参考mysql与oracle分页数字的区别")
                            .build()
            );

            configParam.addField(
                    DropdownField.toBuilder(P_PAGE_TYPE, "分页类型", PAGE_TYPE_START_TO_SIZE, PAGE_TYPES)
                            .required(true)
                            .description("分页类型/参考mysql与oracle分页数字的区别")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            P_PAGE_SIZE, "页大小", pageSize)
                            .description("当前查询的页大小")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(true)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            P_PAGE_START_NUM, "起始页数", pageStartNum)
                            .description("第一次查询的起始页数, 默认：" + pageType)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(false)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            P_PAGE_DELAY_MS, "延迟查询", pageStartNum)
                            .description("分页延迟毫秒数, 每一次查询时间间隔, 默认：" + pageDelayMs + "ms")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(false)
                            .build()
            );

        }

        /**
         * 解决lambda 无法修改变量的问题
         */
        private int currentNum;


        public int getCurrentNum() {
            return currentNum;
        }

        public void setCurrentNum(int currentNum) {
            this.currentNum = currentNum;
        }
    }
}

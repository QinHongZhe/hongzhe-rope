package com.starblues.rope.system.initializers.support.migration.suport;

import com.google.common.collect.Sets;
import com.starblues.rope.system.initializers.support.migration.AbstractMigration;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 基本表的迁移对象
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class BaseMigration extends AbstractMigration {

    /**
     * 由于h2数据库SCHEMA查询时, SCHEMA 需要携带引号。所有在此处携带引号
     */
    public static final String SCHEMA = "\"system_base\"";


    @Override
    public Set<String> locations() {
        return Sets.newHashSet("classpath:db");
    }

    @Override
    public String description() {
        return "初始化系统基本表";
    }

    @Override
    public String schema() {
        return SCHEMA.replace("\"", "");
    }

}

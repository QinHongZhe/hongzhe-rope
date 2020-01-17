package com.starblues.rope.system.initializers.support.migration;

import com.google.common.collect.Sets;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 抽象的表迁移类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractMigration {



    public final String[] getLocations(){
        Set<String> locations = locations();
        if(locations == null || locations.isEmpty()){
            return new String[]{};
        } else {
            return locations.toArray(new String[locations.size()]);
        }
    }

    /**
     * sql 文件的位置.
     * classpath: db/migration
     * filesystem: D://db/migration
     * @return String.
     */
    protected Set<String> locations(){
        return Sets.newHashSet("classpath:db.migration");
    }

    /**
     * classLoader
     * @return 默认为当前对象的ClassLoader
     */
    public ClassLoader classLoader(){
        return this.getClass().getClassLoader();
    }


    /**
     * sql文件编码. 默认utf-8
     */
    public Charset encoding(){
        return StandardCharsets.UTF_8;
    }

    /**
     * 迁移者的描述
     * @return String
     */
    public abstract String description();

    /**
     * 返回迁移表名称
     * @return String
     */
    public abstract String schema();


}

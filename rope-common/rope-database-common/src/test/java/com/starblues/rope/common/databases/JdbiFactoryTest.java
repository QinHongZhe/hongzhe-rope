package com.starblues.rope.common.databases;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.jdbi.v3.core.Jdbi;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class JdbiFactoryTest {

    @Ignore
    @Test
    public void simpleTest(){
        Set<String> jars = new HashSet<>();
        jars.add("D:\\code-workspace\\java\\aiops-git\\aiops-collector\\aiops-data-transfer\\plugins\\databases-plugin\\jdbc-jar\\mysql.jar");
        try {
            JdbiFactory dataBases = new JdbiFactory(jars);


            Properties prop = new Properties();
            prop.setProperty(DruidDataSourceFactory.PROP_URL, "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8");
            prop.setProperty(DruidDataSourceFactory.PROP_USERNAME, "root");
            prop.setProperty(DruidDataSourceFactory.PROP_PASSWORD, "123456");
            prop.setProperty(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, "com.mysql.jdbc.Driver");
            Jdbi jdbi = dataBases.createJdbi("test", prop);
//            List<Map<String, Object>> list = jdbi.withHandle(handle -> {
//                return handle.select("select * from data_type")
//                        .mapToMap()
//                        .list();
//            });
//            System.out.println(list);
            Map<String, Object> map = new HashMap<>();
            map.put("age", 12);
            map.put("data", null);
            map.put("time", null);
            jdbi.useHandle(handle -> {
                handle.createUpdate("insert into zhuo (age,data,time) values (:age, :data, :time)")
                        .bindMap(map)
                        .execute();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

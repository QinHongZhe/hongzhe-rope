package com.starblues.rope.plugins.databases.config;

import com.gitee.starblues.annotation.ConfigDefinition;
import lombok.Data;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@ConfigDefinition("databases-plugin.yml")
@Data
public class DatabasesConfig {

    private List<DatabaseConfig> databases;


}

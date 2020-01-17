package com.starblues.rope.plugins.databases;

import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据库插件
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DatabasesPlugin extends BasePlugin implements StaticResourceConfig {

    private static final String DOC_PATH = "doc";
    public static String pluginId;

    private final Set<String> locations = new HashSet<>();

    public DatabasesPlugin(PluginWrapper wrapper) {
        super(wrapper);
        locations.add("classpath:" + DOC_PATH);
        pluginId = wrapper.getPluginId();
    }


    @Override
    public Set<String> locations() {
        return locations;
    }
}

package com.starblues.rope.system.initializers.support;

import com.gitee.starblues.integration.application.PluginApplication;
import com.starblues.rope.system.initializers.AbstractInitializer;
import com.starblues.rope.system.initializers.support.migration.PluginMigrationInitializer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 插件初始化者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class PluginInitializer extends AbstractInitializer {

    private final PluginApplication pluginApplication;
    private final PluginMigrationInitializer pluginMigrationInitializer;

    private ApplicationContext applicationContext;


    public PluginInitializer(PluginApplication pluginApplication,
                             PluginMigrationInitializer pluginMigrationInitializer) {
        this.pluginApplication = pluginApplication;
        this.pluginMigrationInitializer = pluginMigrationInitializer;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected String name() {
        return "plugin";
    }

    @Override
    protected void start() throws Exception {
        pluginApplication.addListener(pluginMigrationInitializer);
        pluginApplication.initialize(applicationContext, null);
    }

    @Override
    protected void stop() throws Exception {

    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}

package com.starblues.rope.system.initializers.support.migration;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基本的表迁移
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class PluginMigrationInitializer implements PluginListener {

    private final PluginApplication pluginApplication;
    private final MigrationRunner migrationRunner;

    public PluginMigrationInitializer(PluginApplication pluginApplication,
                                      MigrationRunner migrationRunner) {
        this.pluginApplication = pluginApplication;
        this.migrationRunner = migrationRunner;
    }


    @Override
    public void registry(String pluginId) {
        List<AbstractMigration> migrations = pluginApplication.getPluginUser()
                .getPluginBeans(pluginId, AbstractMigration.class);
        PluginOperator pluginOperator = pluginApplication.getPluginOperator();
        PluginWrapper pluginWrapper = pluginOperator.getPluginWrapper(pluginId);
        migrationRunner.run(log, migrations, pluginWrapper.getPluginClassLoader());
    }

    @Override
    public void unRegistry(String pluginId) {

    }

    @Override
    public void failure(String pluginId, Throwable throwable) {

    }
}

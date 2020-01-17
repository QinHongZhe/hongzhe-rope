package com.starblues.rope.system.initializers.support.migration;

import com.google.common.collect.Lists;
import com.starblues.rope.system.initializers.AbstractInitializer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 基本的表迁移
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class BaseMigrationInitializer extends AbstractInitializer {

    private final List<AbstractMigration> migrations;
    private final MigrationRunner migrationRunner;

    public BaseMigrationInitializer(ApplicationContext applicationContext,
                                    MigrationRunner migrationRunner) {
        this.migrationRunner = migrationRunner;
        Map<String, AbstractMigration> beansOfType = applicationContext
                .getBeansOfType(AbstractMigration.class);
        this.migrations = Lists.newArrayList(beansOfType.values());
    }

    @Override
    protected String name() {
        return "base-migration";
    }


    @Override
    protected void start() throws Exception {
        migrationRunner.run(log, migrations);
    }

    @Override
    protected void stop() throws Exception {

    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}

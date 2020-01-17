package com.starblues.rope.system.initializers.support.migration;

import com.google.common.collect.Sets;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * Migration 执行者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class MigrationRunner {

    private final DataSource dataSource;
    private final Set<String> schemas = Sets.newHashSet();

    public MigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void run(Logger logger, List<AbstractMigration> migrations){
        run(logger, migrations, null);
    }

    public void run(Logger logger, List<AbstractMigration> migrations, ClassLoader classLoader){
        if(migrations == null || migrations.isEmpty()){
            return;
        }
        for (AbstractMigration migration : migrations) {
            if(migration == null){
                return;
            }
            String schema = migration.schema();
            if(schemas.contains(schema)){
                logger.error("Migration {}:{} error. The migration schema '{}' already exists",
                        migration.getClass().getName(),
                        migration.description(),
                        schema);
                continue;
            }
            Flyway flyway = null;
            try {
                flyway = Flyway.configure(classLoader == null ? migration.classLoader() : classLoader)
                        .encoding(migration.encoding())
                        .validateOnMigrate(true)
                        .locations(migration.getLocations())
                        .baselineOnMigrate(true)
                        .schemas(schema)
                        .dataSource(dataSource)
                        .outOfOrder(true)
                        .load();
                flyway.migrate();
                schemas.add(schema);
            } catch (Exception e){
                if(flyway != null){
                    flyway.repair();
                }
                logger.error("Migration {}:{} error. {}", migration.getClass().getName(),
                        migration.description(),
                        e.getMessage(),
                        e);
                e.printStackTrace();
            }
        }
    }
}

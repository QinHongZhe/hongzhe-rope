package com.starblues.rope.system.initializers;

import com.google.common.collect.Lists;
import com.starblues.rope.system.initializers.support.PeriodicalInitializer;
import com.starblues.rope.system.initializers.support.ProcessInitializer;
import com.starblues.rope.system.initializers.support.RepositoryInitializer;
import com.starblues.rope.system.initializers.support.PluginInitializer;
import com.starblues.rope.system.initializers.support.migration.BaseMigrationInitializer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 系统初始化者工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SystemInitializerFactory implements ApplicationContextAware,
        ApplicationListener<ContextClosedEvent> {

    private ApplicationContext applicationContext;


    private final List<AbstractInitializer> initializers = Lists.newArrayList();

    private final PluginInitializer pluginInitializer;


    public SystemInitializerFactory(RepositoryInitializer repositoryInitializer,
                                    BaseMigrationInitializer baseMigrationInitializer,
                                    PluginInitializer pluginInitializer,
                                    PeriodicalInitializer periodicalInitializer,
                                    ProcessInitializer processInitializer) {
        this.pluginInitializer = pluginInitializer;

        initializers.add(repositoryInitializer);
        initializers.add(baseMigrationInitializer);
        initializers.add(pluginInitializer);
        initializers.add(periodicalInitializer);
        initializers.add(processInitializer);
    }


    @PostConstruct
    public void init() throws Exception{
        if(applicationContext == null){
            throw new RuntimeException("ApplicationContext is null, The system cannot be initialized");
        }
        // 正序初始化
        for (AbstractInitializer initializer : initializers) {
            initializer.initialize();
        }
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        // 倒序停止
        for (int i = initializers.size() - 1; i >= 0; i--) {
            try {
                AbstractInitializer initializer = initializers.get(i);
                initializer.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        pluginInitializer.setApplicationContext(applicationContext);
    }

}

package com.starblues.rope.config;

import com.gitee.starblues.extension.resources.StaticResourceExtension;
import com.gitee.starblues.integration.ConfigurationBuilder;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.application.DefaultPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 插件集成配置
 *
 * @author zhangzhuo 李静
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginConfig {

    /**
     * 插件路径访问前缀
     */
    public static final String PLUGIN_PATH_PREFIX = "static";


    /**
     * 运行模式
     *  开发环境: development、dev
     *  生产/部署 环境: deployment、prod
     */
    @Value("${runMode:dev}")
    private String runMode;

    /**
     * 插件的路径
     */
    @Value("${pluginPath:plugins}")
    private String pluginPath;

    /**
     * 插件文件的路径
     */
    @Value("${pluginConfigFilePath:pluginConfigs}")
    private String pluginConfigFilePath;

    /**
     * 在卸载插件后, 备份插件的目录
     */
    @Value("${backupPluginPath:backupPlugin}")
    private String backupPluginPath;

    /**
     * 上传的插件所存储的临时目录
     */
    @Value("${uploadTempPath:temp}")
    private String uploadTempPath;


    @Bean
    public IntegrationConfiguration configuration(){
        return ConfigurationBuilder.toBuilder()
                .runtimeMode(RuntimeMode.byName(runMode))
                .pluginPath(pluginPath)
                .pluginConfigFilePath(pluginConfigFilePath)
                .uploadTempPath(uploadTempPath)
                .backupPath(backupPluginPath)
                .pluginRestControllerPathPrefix("/api/plugin")
                .enablePluginIdRestControllerPathPrefix(true)
                .build();
    }


    /**
     * 定义插件应用。使用可以注入它操作插件。
     * @return PluginApplication
     */
    @Bean
    public PluginApplication pluginApplication(){
        // 实例化自动初始化插件的PluginApplication
        PluginApplication pluginApplication = new DefaultPluginApplication();
        StaticResourceExtension staticResourceExtension = new StaticResourceExtension();
        // 设置路径访问前缀
        staticResourceExtension.setPathPrefix(PLUGIN_PATH_PREFIX);
        // 设置为不进行缓存
        staticResourceExtension.setCacheControl(null);
        pluginApplication.addExtension(staticResourceExtension);
        return pluginApplication;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public void setPluginConfigFilePath(String pluginConfigFilePath) {
        this.pluginConfigFilePath = pluginConfigFilePath;
    }
}

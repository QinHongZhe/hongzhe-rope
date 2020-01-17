package com.starblues.rope.rest.plugin;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.utils.TextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

/**
 * 插件管理接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API + "plugin")
@Api(value = "插件接口",description = "主要管理插件的启停、安装、卸载等")
public class PluginResource extends BaseResource{

    private final PluginApplication pluginApplication;


    public PluginResource(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
    }


    @ApiOperation(value = "获取插件信息", notes = "获取插件的详细信息", consumes = "application/json")
    @GetMapping
    public Result<List<PluginInfo>> getPluginInfo(){
        List<PluginInfo> pluginInfos = pluginOperator().getPluginInfo();
        return responseBody(Result.ResponseEnum.GET_SUCCESS, pluginInfos);
    }

    @ApiOperation(value = "启动插件", notes = "通过插件id启动插件", consumes = "application/json")
    @ApiImplicitParam(name = "pluginId", value = "插件id", required = true,
            dataTypeClass = String.class, paramType = "path")
    @PostMapping("start/{pluginId}")
    public Result<List<PluginInfo>> start(@PathVariable("pluginId") String pluginId){
        try {
            if(pluginOperator().start(pluginId)){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS,
                        TextUtils.format("启动插件 '{}' 成功", pluginId));
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR,
                        TextUtils.format("启动插件 '{}' 失败", pluginId));
            }        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR,
                    TextUtils.format("启动插件 '{}' 失败 : {}", pluginId, e.getMessage()));
        }
    }


    @ApiOperation(value = "停止插件", notes = "通过插件id停止插件", consumes = "application/json")
    @ApiImplicitParam(name = "pluginId", value = "插件id", required = true, dataTypeClass = String.class,
            paramType = "path")
    @PostMapping("stop/{pluginId}")
    public Result<List<PluginInfo>> stop(@PathVariable("pluginId") String pluginId){
        try {
            if(pluginOperator().stop(pluginId)){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS,
                        TextUtils.format("停止插件 '{}' 成功", pluginId));
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR,
                        TextUtils.format("停止插件 '{}' 失败", pluginId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR,
                    TextUtils.format("停止插件 '{}' 失败 : {}", pluginId, e.getMessage()));
        }
    }


    @ApiOperation(value = "安装插件", notes = "上传并安装插件", consumes = "application/json")
    @ApiImplicitParam(name = "file", value = "插件文件-MultipartFile", required = true,
            dataTypeClass = MultipartFile.class, paramType = "form")
    @PostMapping("upload")
    public Result<List<PluginInfo>> uploadInstallPlugin(@RequestParam("file") MultipartFile file){
        try {
            if(pluginOperator().uploadPluginAndStart(file)){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "上传安装插件成功");
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "上传安装插件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "上传安装插件失败 : " + e.getMessage());
        }
    }


    @ApiOperation(value = "安装插件", notes = "通过本地插件文件安装插件", consumes = "application/json")
    @ApiImplicitParam(name = "pluginPath", value = "本地插件路径", required = true, dataTypeClass = String.class,
                paramType = "form")
    @PostMapping("install")
    public Result<List<PluginInfo>> installPlugin(@RequestParam("pluginPath") String pluginPath){
        try {
            if(pluginOperator().install(Paths.get(pluginPath))){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "安装插件成功");
            }  else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "安装插件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "安装插件失败: " + e.getMessage());
        }
    }


    @ApiOperation(value = "安装插件配置文件", notes = "上传并安装插件的配置文件")
    @ApiImplicitParam(name = "file", value = "插件配置文件-MultipartFile", required = true,
            dataTypeClass = MultipartFile.class, paramType = "form")
    @PostMapping("upload/config")
    public Result<List<PluginInfo>> uploadInstallPluginConfig(@RequestParam("file") MultipartFile file){
        try {
            if(pluginOperator().uploadConfigFile(file)){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "上传安装插件的配置文件成功");
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "上传安装插件的配置文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "上传安装插件的配置文件失败 : " + e.getMessage());
        }
    }


    @ApiOperation(value = "安装插件的配置文件", notes = "在本地安装插件的配置文件")
    @ApiImplicitParam(name = "configPath", value = "本地插件的配置文件路径", required = true,
            dataTypeClass = String.class, paramType = "form")
    @PostMapping("install/config")
    public Result<List<PluginInfo>> installPluginConfig(@RequestParam("configPath") String configPath){
        try {
            if(pluginOperator().installConfigFile(Paths.get(configPath))){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "安装插件的配置文件");
            }  else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "安装插件的配置文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "安装插件的配置文件失败: " + e.getMessage());
        }
    }


    @ApiOperation(value = "卸载插件", notes = "通过插件id卸载插件", consumes = "application/json")
    @ApiImplicitParam(name = "pluginId", value = "插件id", required = true, dataTypeClass = String.class,
            paramType = "path")
    @PostMapping("uninstall/{pluginId}")
    public Result<List<PluginInfo>> uninstall(@PathVariable("pluginId") String pluginId){
        try {
            if(pluginOperator().uninstall(pluginId, true)){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "卸载插件成功");
            }  else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "卸载插件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "卸载插件失败: " + e.getMessage());
        }
    }


    private PluginOperator pluginOperator(){
        return pluginApplication.getPluginOperator();
    }

}

package com.starblues.rope.rest.process;

import com.google.gson.Gson;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.process.store.ProcessStorage;
import com.starblues.rope.process.store.ProcessStorageFactory;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * 流程存储接口。主要控制流程的创建、修改、删除、修改、查看等
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API +  "/processStorage")
@Api(value = "流程存储",description = "流程存储的相关接口")
public class ProcessStorageResource extends BaseResource {

    private final ProcessStorageFactory processStorageFactory;
    private final ProcessFactory processFactory;
    private final Gson gson;

    public ProcessStorageResource(ProcessStorageFactory processStorageFactory,
                                  ProcessFactory processFactory,
                                  Gson gson) {
        this.processStorageFactory = processStorageFactory;
        this.processFactory = processFactory;
        this.gson = gson;
    }

    @ApiOperation( value = "得到流程存储者的信息", notes = "得到系统配置的流程存储者的信息", consumes = "application/json")
    @GetMapping()
    public Result<List<ProcessStorageFactory.ProcessStorageInfo>> getProcessStorageInfo() {
        return responseBody(Result.ResponseEnum.GET_SUCCESS, processStorageFactory.getProcessStorageInfo());
    }


    @ApiOperation( value = "得到流程信息", notes = "通过流程存储者id和流程id得到流程信息", consumes = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "processStorageId", value = "流程存储者id",
                    required = true, dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(
                    name = "processId", value = "流程id",
                    required = true, dataTypeClass = String.class, paramType = "path")
    })
    @GetMapping("processInfo/{processStorageId}/{processId}")
    public Result<ProcessStorage.ProcessInfo> getProcessInfo(
            @PathVariable("processStorageId") String processStorageId,
            @PathVariable("processId") String processId) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.GET_SUCCESS);
        }
        ProcessStorage.ProcessInfo processInfo = processStorage.getProcessInfo(processId);
        return responseBody(Result.ResponseEnum.GET_SUCCESS, processInfo);
    }


    @ApiOperation( value = "得到流程信息", notes = "通过流程存储者id得到流程信息集合", consumes = "application/json")
    @ApiImplicitParam(
            name = "processStorageId", value = "流程存储者id",
            required = true, dataTypeClass = String.class, paramType = "path")
    @GetMapping("processInfo/{processStorageId}")
    public Result<List<ProcessStorage.ProcessInfo>> getProcessInfos(
            @PathVariable("processStorageId") String processStorageId) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.GET_SUCCESS, Collections.emptyList());
        }
        List<ProcessStorage.ProcessInfo> processInfos = processStorage.getProcessInfos();
        return responseBody(Result.ResponseEnum.GET_SUCCESS, processInfos);
    }


    @ApiOperation( value = "创建流程", notes = "下载流程文件", consumes = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "processStorageId", value = "流程存储者id",
                    required = true, dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(
                    name = "processConfig", value = "流程配置bean",
                    required = true, dataTypeClass = ProcessConfig.class, paramType = "body")
    })
    @PostMapping("{processStorageId}")
    public Result create(@PathVariable("processStorageId") String processStorageId,
                         @RequestBody ProcessConfig processConfig) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.CREATE_ERROR, "没有发现该流程存储者：" + processStorageId);
        }
        try {
            processStorage.create(processConfig);
            return responseBody(Result.ResponseEnum.CREATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.CREATE_ERROR, "创建流程失败: " + e.getMessage());
        }
    }


    @ApiOperation( value = "更新流程", notes = "更新流程", consumes = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "processStorageId", value = "流程存储者id",
                    required = true, dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(
                    name = "processConfig", value = "流程配置bean, 参考文档说明",
                    required = true, dataTypeClass = ProcessConfig.class, paramType = "body")
    })
    @PutMapping("{processStorageId}")
    public Result update(@PathVariable("processStorageId") String processStorageId,
                         @RequestBody ProcessConfig processConfig) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.UPDATE_ERROR, "没有发现该流程存储者：" + processStorageId);
        }
        try {
            processStorage.update(processConfig);
            return responseBody(Result.ResponseEnum.UPDATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.UPDATE_ERROR, "更新流程失败: " + e.getMessage());
        }
    }



    @ApiOperation( value = "删除流程", notes = "根据流程存储者id和流程id来删除流程", consumes = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "processStorageId", value = "流程存储者id",
                    required = true, dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(
                    name = "processId", value = "流程id",
                    required = true, dataTypeClass = String.class, paramType = "path")
    })
    @DeleteMapping("{processStorageId}/{processId}")
    public Result delete(@PathVariable("processStorageId") String processStorageId,
                         @PathVariable("processId") String processId) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.DELETE_ERROR, "没有发现该流程存储者：" + processStorageId);
        }
        try {
            processStorage.delete(processId);
            return responseBody(Result.ResponseEnum.DELETE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.DELETE_ERROR, "删除流程失败: " + e.getMessage());
        }
    }

    @ApiOperation( value = "导入流程", notes = "通过json文件导入流程")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "processStorageId", value = "流程存储者id",
                    required = true, dataTypeClass = String.class, paramType = "path"),
            @ApiImplicitParam(
                    name = "jsonFile", value = "流程json文件",
                    required = true, dataTypeClass = MultipartFile.class, paramType = "MultipartFile")
    })
    @PostMapping("import/{processStorageId}")
    public Result update(@PathVariable("processStorageId") String processStorageId,
                         @RequestParam("file") MultipartFile file) {
        ProcessStorage processStorage = processStorageFactory.getProcessStorage(processStorageId);
        if(processStorage == null){
            return responseBody(Result.ResponseEnum.UPDATE_ERROR, "没有发现该流程存储者：" + processStorageId);
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(file.getInputStream());
            ProcessConfig processConfig = gson.fromJson(inputStreamReader, ProcessConfig.class);
            processStorage.create(processConfig);
            return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "导入流程 '"
                    + processConfig.getProcessId() + "'成功");
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_SUCCESS, "导入流程失败: " + e.getMessage());
        } finally {
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @ApiOperation(value = "下载流程", notes = "将流程下载为json文件。只能下载系统所启动的流程", consumes = "application/json")
    @ApiImplicitParam(
            name = "processId", value = "流程id",
            required = true, dataTypeClass = String.class, paramType = "path")
    @GetMapping("download/{processId}")
    public Result download(@PathVariable("processId") String processId,
                           HttpServletResponse response) {
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager == null){
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "没有发现该流程：" + processId);
        }
        OutputStream stream = null;
        try {
            ProcessConfig processConfig = processManager.getProcessConfig();
            String json = gson.toJson(processConfig);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            stream = response.getOutputStream();
            stream.write(bytes);

            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + processId + ".json");
            response.addHeader("Content-Length", "" + bytes.length);
            response.setContentType("application/octet-stream");
            stream.flush();
            return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "下载失败: " + e.getMessage());
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

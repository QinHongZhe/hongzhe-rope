package com.starblues.rope.rest.process;

import com.starblues.rope.process.store.ProcessStorage;
import com.starblues.rope.process.store.ProcessStorageFactory;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.service.process.ProcessService;
import com.starblues.rope.service.process.model.ProcessInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程管理接口。主要用于控制系统已经被添加的流程
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API + "/process")
@Api(value = "流程管理",description = "操作流程的相关接口")
public class ProcessResource extends BaseResource {

    private final ProcessService processService;
    private final ProcessStorageFactory processStorageFactory;

    public ProcessResource(ProcessService processService, ProcessStorageFactory processStorageFactory) {
        this.processService = processService;
        this.processStorageFactory = processStorageFactory;
    }


    @ApiOperation(value = "获取所有的流程信息", notes = "流程信息包括: 运行中的流程、停止的流程",
            consumes = "application/json")
    @GetMapping()
    public Result<List<ProcessInfo>> getProcess() {
         return responseBody(Result.ResponseEnum.GET_SUCCESS, processService.getAllProcess());
    }


    @ApiOperation(value = "启动流程", notes = "根据流程id启动流程", consumes = "application/json")
    @ApiImplicitParam(name = "processId", value = "流程id", required = true, dataTypeClass = String.class, paramType = "path")
    @PostMapping("/start/{processId}")
    public Result<List<ProcessInfo>> start(@PathVariable("processId") String processId) {
        try {
            processService.start(processId);
            return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
        }
    }


    @ApiOperation(value = "停止流程", notes = "根据流程id停止流程", consumes = "application/json")
    @ApiImplicitParam(name = "processId", value = "流程id", required = true, dataTypeClass = String.class, paramType = "path")
    @PostMapping("/stop/{processId}")
    public Result<List<ProcessInfo>> stop(@PathVariable("processId") String processId) {
        try {
            processService.stop(processId);
            return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
        }
    }


    @ApiOperation(value = "删除流程", notes = "根据流程id停止流程", consumes = "application/json")
    @ApiImplicitParam(name = "processId", value = "流程id", required = true, dataTypeClass = String.class, paramType = "path")
    @PostMapping("/remove/{processId}")
    public Result<List<ProcessInfo>> delete(@PathVariable("processId") String processId) {
        try {
            boolean result = processStorageFactory.delete(processId);
            if(result){
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "删除流程失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
        }
    }


    @ApiOperation(value = "统计流程", notes = "统计流程的信息", consumes = "application/json")
    @GetMapping("/count")
    public Result count(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, processService.count());
    }


}

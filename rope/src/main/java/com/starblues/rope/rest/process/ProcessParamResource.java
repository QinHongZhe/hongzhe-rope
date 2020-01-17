package com.starblues.rope.rest.process;

import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.service.process.ProcessParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程参数的接口。主要返回配置流程时所需的参数信息。用于动态构建界面表单
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API + "processParam")
@Api(value = "流程参数接口",description = "主要返回配置流程时所需的参数信息。用于动态构建界面表单")
public class ProcessParamResource extends BaseResource {

    private final ProcessParamService inputService;

    public ProcessParamResource(ProcessParamService inputService) {
        this.inputService = inputService;
    }


    @GetMapping("/inputs")
    @ApiOperation(value="输入配置说明", notes="获取系统中所有的输入所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ParamInfo>> getInputInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getInputConfigParam());
    }


    @GetMapping("/outputs")
    @ApiOperation(value="输出配置说明", notes="获取系统中所有的输出所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ParamInfo>> getOutputInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getOutputConfigParam());
    }


    @GetMapping("/readers")
    @ApiOperation(value="读取者配置说明", notes="获取系统中所有的数据读取者所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ParamInfo>> getReaderInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getReaderConfigParam());
    }


    @GetMapping("/writers")
    @ApiOperation(value="写入者配置说明", notes="获取系统中所有的数据写入者所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ParamInfo>> getWriterInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getWriterConfigParam());
    }


    @GetMapping("/dataHandlers")
    @ApiOperation(value="数据处理者配置说明", notes="获取系统中所有的数据处理者所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ParamInfo>> getDataHandlerInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getDataHandlerConfigParam());
    }


    @GetMapping("/inputConverters")
    @ApiOperation(value="输入数据转换器配置说明", notes="获取系统中所有的输入数据转换器所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ConverterParamInfo>> getInputConverterInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getInputConverterConfigParam());
    }

    @GetMapping("/writerConverters")
    @ApiOperation(value="写入者数据转换器配置说明", notes="获取系统中所有的写入者数据转换器所需配置参数和说明信息",
            consumes = "application/json")
    public Result<List<ProcessParamService.ConverterParamInfo>> getWriterConverterInfo(){
        return responseBody(Result.ResponseEnum.GET_SUCCESS, inputService.getWriterConverterConfigParam());
    }

}

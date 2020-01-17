package com.starblues.rope.rest.metrics;

import com.google.common.collect.Maps;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.service.metrics.ThroughputService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标监控的接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(BaseResource.API + "metrics")
@Api(value = "指标监控",description = "主要获取指标监控的接口")
    public class MetricsResource extends BaseResource {


    private final ThroughputService throughputService;

    public MetricsResource(ThroughputService throughputService) {
        this.throughputService = throughputService;
    }


    @GetMapping("overview")
    public Result overview(){
        HashMap<String, List<Map<String, Object>>> overview = Maps.newHashMap();
        overview.put("input", throughputService.getInputTransport());
        overview.put("output", throughputService.getOutputTransport());
        overview.put("writers", throughputService.getWriters());
        return responseBody(Result.ResponseEnum.GET_SUCCESS, overview);
    }


    @GetMapping("input")
    public Result inputThroughput(){
        return getResult(throughputService.getInputTransport());
    }


    @GetMapping("reader/{processId}")
    public Result inputThroughput(@PathVariable("processId") String processId){
        return getResult(throughputService.getReader(processId));
    }


    @GetMapping("output")
    public Result outputThroughput(){
        return getResult(throughputService.getOutputTransport());
    }

    @GetMapping("writers")
    public Result writersThroughput(){
        return getResult(throughputService.getWriters());
    }


    @GetMapping("writers/{processId}")
    public Result writersThroughput(@PathVariable("processId") String processId){
        return getResult(throughputService.getWriters(processId));
    }


    /**
     * 获取结果
     * @param data 数据
     * @return 响应结果
     */
    private Result getResult(Collection data) {
        if (data != null && !data.isEmpty()) {
            return responseBody(Result.ResponseEnum.GET_SUCCESS, data);
        } else {
            return responseBody(Result.ResponseEnum.GET_ERROR, "没有发现吞吐量");
        }
    }
}

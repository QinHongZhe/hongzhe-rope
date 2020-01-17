package com.starblues.rope.service.metrics;

import java.util.List;
import java.util.Map;

/**
 * 吞吐量 Service
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ThroughputService {

    /**
     * 得到输入的Throughput的吞吐量
     * @return Map
     */
    List<Map<String, Object>> getInputTransport();


    /**
     * 得到输入的吞吐量
     * @param processId 流程id
     * @return Map
     */
    List<Map<String, Object>> getReader(String processId);

    /**
     * 得到输出的Throughput的吞吐量
     * @return Map
     */
    List<Map<String, Object>> getOutputTransport();

    /**
     * 得到所有写入者的吞吐量
     * @param processId 流程id
     * @return Map
     */
    List<Map<String, Object>> getWriters(String processId);

    /**
     * 得到所有写入者的吞吐量
     * @return Map
     */
    List<Map<String, Object>> getWriters();

}

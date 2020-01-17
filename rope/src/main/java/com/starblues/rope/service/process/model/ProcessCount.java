package com.starblues.rope.service.process.model;

import lombok.Data;

/**
 * 流程统计的bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class ProcessCount {


    private int count = 0;
    private int startCount = 0;
    private int stopCount = 0;


}

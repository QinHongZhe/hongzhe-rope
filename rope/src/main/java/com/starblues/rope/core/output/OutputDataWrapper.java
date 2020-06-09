package com.starblues.rope.core.output;

import lombok.Data;

import java.util.List;

/**
 * 输出数据包装者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class OutputDataWrapper {


    /**
     * 源数据。对象
     */
    private List<Object> data;

    /**
     * 该数据的字节大小
     */
    private Long byteSize = 0L;

    private int size;

    public static OutputDataWrapper getInstance(List<Object> data, long byteSize){
        OutputDataWrapper cacheDataWrapper = new OutputDataWrapper();
        cacheDataWrapper.setByteSize(byteSize);
        cacheDataWrapper.setData(data);
        cacheDataWrapper.setSize(data.size());
        return cacheDataWrapper;
    }

    public void clean(){
        data.clear();
        data = null;
    }

}

package com.starblues.rope.core.output.cache;

/**
 * 输出缓存工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface OutputCacheFactory {


    /**
     * 得到缓存操作对象
     * @param globalKey 缓存的全局key
     * @return OutputCache
     */
    OutputCache get(String globalKey);


}

package com.starblues.rope.core.output.cache;

import com.starblues.rope.core.output.OutputDataWrapper;

import java.util.List;

/**
 * 输出数据缓存接口。 必须保证线程安全
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface OutputCache {


    /**
     * 缓存数据
     * @param object 缓存的对象
     */
    void add(Object object);


    /**
     * 批量缓存数据
     * @param dataWrapper 缓存的对象集合的包装
     */
    void add(OutputDataWrapper dataWrapper);


    /**
     * 得到缓存的数据个数
     * @return int
     */
    int getCacheSize();


    /**
     * 得到缓存的数据的字节大小
     * @return long
     */
    long getCacheByteSize();

    /**
     * 得到缓存中的全部数据
     * @return 缓存的值
     */
    List<Object> getAll();


    /**
     * 得到缓存中的全部数据。并且清除数据
     * @return 缓存的值
     */
    List<Object> getAllAndClean();


    /**
     * 清除全部缓存的数据
     */
    void clean();

}

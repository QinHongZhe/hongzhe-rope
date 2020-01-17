package com.starblues.rope.core.output.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 抽象的缓存工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractOutputCacheFactory implements OutputCacheFactory{

    private final Map<String, OutputCache> outputCaches = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);



    @Override
    public OutputCache get(String globalKey) {
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            OutputCache outputCache = outputCaches.get(globalKey);
            if(outputCache != null){
                return outputCache;
            }
        } finally {
            readLock.unlock();
        }
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            OutputCache createOutputCache = create(globalKey);
            if(createOutputCache == null){
                throw new RuntimeException("Create outputCache is null");
            } else {
                outputCaches.put(globalKey, createOutputCache);
            }
            return createOutputCache;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 创建缓存对象
     * @param globalKey 全局缓存对象key
     * @return 缓存对象
     */
    protected abstract OutputCache create(String globalKey);

}

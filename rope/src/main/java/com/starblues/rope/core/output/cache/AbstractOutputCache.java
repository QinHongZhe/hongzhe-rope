package com.starblues.rope.core.output.cache;

import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.OutputDataWrapper;
import org.apache.lucene.util.RamUsageEstimator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 抽象的输出缓存。主要用于计算缓存大小和缓存内存大小
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractOutputCache implements OutputCache {

    private final AtomicInteger size = new AtomicInteger(0);
    private final AtomicLong byteSize = new AtomicLong(0L);
    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    @Override
    public final void add(Object object) {
        if(object == null){
            return;
        }
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            cache(object);
            size.incrementAndGet();
            if(object instanceof Record){
                byteSize.addAndGet(((Record) object).getByteSize());
            } else {
                byteSize.addAndGet(RamUsageEstimator.sizeOfObject(object));
            }
        } finally {
            writeLock.unlock();
        }
    }


    @Override
    public final void add(OutputDataWrapper dataWrapper) {
        if(dataWrapper == null){
            return;
        }
        List<Object> objects = dataWrapper.getData();
        if(objects == null || objects.isEmpty()){
            return;
        }
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            cache(objects);
            size.addAndGet(objects.size());
            byteSize.addAndGet(dataWrapper.getByteSize());
        } finally {
            writeLock.unlock();
        }
    }


    @Override
    public int getCacheSize() {
        return size.get();
    }

    @Override
    public long getCacheByteSize() {
        return byteSize.get();
    }

    @Override
    public List<Object> getAllAndClean() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            List<Object> cacheData = getAllData();
            cleanData();
            flush();
            return cacheData;
        } finally {
            writeLock.unlock();
        }
    }


    @Override
    public final List<Object> getAll() {
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            return getAllData();
        } finally {
            readLock.unlock();
        }
    }


    @Override
    public final void clean() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            cleanData();
            flush();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 缓存对象
     * @param object 对象
     */
    protected abstract void cache(Object object);


    /**
     * 缓存对象
     * @param objects 对象集合
     */
    protected abstract void cache(List<Object> objects);

    /**
     * 获取全部的数据
     * @return 全部缓存的数据
     */
    protected abstract List<Object> getAllData();

    /**
     * 子类清除数据的实现
     */
    protected abstract void cleanData();


    private void flush(){
        size.set(0);
        byteSize.set(0L);
    }

}

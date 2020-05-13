package com.starblues.rope.core.output;

import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.output.cache.OutputCache;
import com.starblues.rope.core.output.cache.OutputCacheFactory;
import com.starblues.rope.core.output.cache.memory.MemoryOutputCacheFactory;
import com.starblues.rope.core.output.writer.Writer;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 存在缓存的输出
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractCacheOutput extends AbstractOutput{

    /**
     * 输出的缓存
     */
    private OutputCacheFactory outputCacheFactory;


    /**
     * 判断数据是否写入中
     */
    private AtomicBoolean writing = new AtomicBoolean(false);


    /**
     * 输出统计
     */
    private AtomicInteger outputCount = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock(true);

    public AbstractCacheOutput() {
        this.outputCacheFactory = new MemoryOutputCacheFactory();
    }


    /**
     * 设置输出缓存工厂
     * @param outputCacheFactory 输出缓存
     */
    public void setOutputCacheFactory(OutputCacheFactory outputCacheFactory){
        if(isRunning()){
            throw new RuntimeException("The output is running and cannot be set outputCacheFactory in process:" + processId());
        }
        if(outputCacheFactory != null){
            this.outputCacheFactory = outputCacheFactory;
        }
    }


    @Override
    public final void output(RecordWrapper recordWrapper) {
        if(!checkRecordWrapper(recordWrapper)){
            return;
        }
        if(emptyWriteWarn()){
            return;
        }
        if(canNotOutput()){
            return;
        }
        int count = 0;
        if(recordWrapper.isLastRecordSigner()){
            // 如果为最后一条记录的标记者, 则直接进行输出
            outputToWrite();
            return;
        }
        for (WriterWrapper writerWrapper : writerWrappers) {
            Writer writer = writerWrapper.getWriter();
            try {
                OutputDataWrapper convertData = convertRecords(writer, recordWrapper);
                OutputDataWrapper dataWrapper = OutputDataWrapper.getInstance(
                        convertData.getData(), convertData.getByteSize()
                );
                getLogger().debug("Convert process[{}] writer[{}] data size is {}",
                        processId(), writerWrapper.getCode(), convertData.getSize());
                if(writing.get()){
                    // 临时的输出缓存。
                    // 当输出缓存被操作输出数据时, 临时缓存会存储数据。
                    // 当时输出缓存被操作完, 则会将临时缓存数据移到输出缓存中
                    OutputCache tempOutputCache = outputCacheFactory.get(getTempCacheKey(writerWrapper));
                    tempOutputCache.add(dataWrapper);
                } else {
                    OutputCache tempOutputCache = outputCacheFactory.get(getTempCacheKey(writerWrapper));
                    OutputCache outputCache = outputCacheFactory.get(getCacheKey(writerWrapper));
                    if(tempOutputCache.getCacheSize() > 0){
                        // 如果临时缓存中存在数据, 则将临时数据新增到正式缓存中
                        long cacheByteSize = tempOutputCache.getCacheByteSize();
                        List<Object> data = tempOutputCache.getAllAndClean();
                        OutputDataWrapper tempDataWrapper = OutputDataWrapper.getInstance(
                                data, cacheByteSize
                        );
                        outputCache.add(tempDataWrapper);
                    }
                    outputCache.add(dataWrapper);
                }
            } catch (Exception e) {
                outputException(writer, e);
            }
        }
        getLogger().debug("process[{}] data size is '{}' in process[{}]",
                processId(), recordWrapper.getRecordGroup().size(), processId());
        count = count + recordWrapper.getRecordGroup().size();
        if(count > 0){
            outputCount.addAndGet(count);
            lock.lock();
            try {
                outputEvent();
            } catch (Exception e){
                getLogger().error("Output event exception in process[{}]. {}", processId(), e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 输出事件.每被调用一次输出则进行一次事件通知。线程安全
     */
    protected abstract void outputEvent();

    /**
     * 输出统计数。每调用一次写入，输出统计置为0
     * @return Integer
     */
    protected final Integer outputCount(){
        return outputCount.get();
    }


    /**
     * 输出缓存中的数据到写入者中
     */
    protected final void outputToWrite(){
        if(emptyWriteWarn()){
            return;
        }
        writing.set(true);
        outputCount.set(0);
        for (WriterWrapper writerWrapper : writerWrappers) {
            Writer writer = writerWrapper.getWriter();
            String cacheKey = getCacheKey(writerWrapper);
            OutputCache outputCache = outputCacheFactory.get(cacheKey);
            long byteSize = outputCache.getCacheByteSize();
            List<Object> convertDataList = outputCache.getAllAndClean();
            if(convertDataList == null || convertDataList.size() == 0){
                continue;
            }
            getLogger().debug("process[{}] writer[{}] write data size is '{}' in process[{}]",
                    processId(), writerWrapper.getCode(), convertDataList.size(), processId());
            OutputDataWrapper convertData = OutputDataWrapper.getInstance(
                    convertDataList, byteSize
            );
            try {
                write(writerWrapper, convertData);
            } catch (Exception e) {
                outputException(writer, e);
            }
        }
        writing.set(false);
    }



    /**
     * 得到缓存的key
     * @param processId 流程id
     * @param writeId 写入者id
     * @return 缓存key
     */
    /**
     * 得到缓存的key
     * @param writerWrapper 写入者包装类
     * @return 缓存的key
     */
    protected String getCacheKey(WriterWrapper writerWrapper){
        return processId() + ":" + writerWrapper.getCode();
    }

    /**
     * 得到临时缓存的key
     * @param writerWrapper 写入者包装类
     * @return 临时缓存的key
     */
    protected String getTempCacheKey(WriterWrapper writerWrapper){
        return getCacheKey(writerWrapper) + ":temp_cache";
    }


}

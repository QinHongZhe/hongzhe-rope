package com.starblues.rope.core.output.cache.memory;

import com.starblues.rope.core.output.cache.AbstractOutputCacheFactory;
import com.starblues.rope.core.output.cache.OutputCache;

/**
 * 内存输出缓存工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class MemoryOutputCacheFactory extends AbstractOutputCacheFactory {


    @Override
    protected OutputCache create(String globalKey) {
        return new MemoryOutputCache();
    }


}

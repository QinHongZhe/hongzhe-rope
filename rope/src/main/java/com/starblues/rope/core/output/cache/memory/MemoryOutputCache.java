package com.starblues.rope.core.output.cache.memory;

import com.starblues.rope.core.output.cache.AbstractOutputCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存型缓存
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class MemoryOutputCache extends AbstractOutputCache {

    private List<Object> objects = new ArrayList<>();


    @Override
    protected void cache(Object object) {
        this.objects.add(object);
    }

    @Override
    protected void cache(List<Object> objects) {
        this.objects.addAll(objects);
    }

    @Override
    protected List<Object> getAllData() {
        return this.objects;
    }

    @Override
    protected void cleanData() {
        this.objects = new ArrayList<>();
    }

}

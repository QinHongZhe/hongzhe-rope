package com.starblues.rope.core.common.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.listener.Listener;
import com.starblues.rope.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 抽象的管理者工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public abstract class AbstractManagerFactory<T extends Managed> implements Manager<T> {


    private final List<Manager<T>> managers = Lists.newArrayList();

    public AbstractManagerFactory() {
    }

    /**
     * 初始化输入管理器
     */
    public abstract void initialize();

    /**
     * 管理者工厂名称
     * @return 名称
     */
    protected abstract String name();


    /**
     * 添加管理者
     * @param manager 管理者
     */
    protected final void addManager(Manager manager){
        if(manager != null){
            this.managers.add(manager);
        }
    }


    @Override
    public boolean support(Object t) {
        if(t == null){
            return false;
        }
        for (Manager<T> manager : managers) {
            if(manager.support(t)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void add(T t) throws Exception {
        if(t == null){
            return;
        }
        for (Manager<T> manager : managers) {
            if(manager.support(t)){
                manager.add(t);
                return;
            }
        }
        String errorMsg = TextUtils.format("Add {} failure. The '{}' type is not supported",
                name(), t.id());
        throw new Exception(errorMsg);
    }

    @Override
    public boolean exist(String id) {
        if(StringUtils.isEmpty(id)){
            return false;
        }
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove(String id) throws Exception {
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Remove failure. id can't be empty");
        }
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                manager.remove(id);
                return;
            }
        }
        String errorMsg = TextUtils.format("Remove {} failure. The '{}' was not found",
                name(), id);
        throw new Exception(errorMsg);
    }

    @Override
    public void start(String id) throws Exception {
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Start " + name() + " failure. id can't be empty");
        }
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                manager.start(id);
                return;
            }
        }
        String errorMsg = TextUtils.format("Start {} failure. The '{}' was not found",
                name(), id);
        throw new Exception(errorMsg);
    }

    @Override
    public void start(T t) throws Exception {
        if(t == null){
            throw new IllegalArgumentException("Start " + name() + " failure. bean can't be null");
        }
        for (Manager<T> manager : managers) {
            if(manager.support(t)){
                manager.start(t);
                return;
            }
        }
        String errorMsg = TextUtils.format("Start {} failure. The '{}' type cannot be supported",
                name(), t.id());
        throw new Exception(errorMsg);
    }

    @Override
    public void stop(String id) throws Exception {
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Stop " + name() + " failure. id can't be empty");
        }
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                manager.stop(id);
                return;
            }
        }
        String errorMsg = TextUtils.format("Stop {} failure. The '{}' was not found",
                name(), id);
        throw new Exception(errorMsg);
    }

    @Override
    public void startAll(Listener listener) {
        for (Manager<T> manager : managers) {
            manager.startAll(listener);
        }
    }

    @Override
    public void stopAll(Listener listener) {
        for (Manager<T> manager : managers) {
            manager.stopAll(listener);
        }
    }

    @Override
    public State getState(String id) {
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                return manager.getState(id);
            }
        }
        return null;
    }

    @Override
    public T get(String id) {
        for (Manager<T> manager : managers) {
            if(manager.exist(id)){
                return manager.get(id);
            }
        }
        return null;
    }

    @Override
    public Set<String> getIds() {
        Set<String> allIds = Sets.newHashSet();
        for (Manager<T> manager : managers) {
            Set<String> ids = manager.getIds();
            if(ids != null && !ids.isEmpty()){
                allIds.addAll(ids);
            }
        }
        return allIds;
    }


}

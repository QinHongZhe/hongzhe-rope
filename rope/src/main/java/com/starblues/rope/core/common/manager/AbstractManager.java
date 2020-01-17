package com.starblues.rope.core.common.manager;

import com.starblues.rope.core.common.ChildLogger;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.listener.Listener;
import com.starblues.rope.utils.TextUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 抽象的管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractManager<T extends Managed>
        implements Manager<T>, ChildLogger {


    private final Class<T> managedClass;

    private final Map<String, T> managedMap = new ConcurrentHashMap<>();

    public AbstractManager() {
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        managedClass = (Class)params[0];
    }

    /**
     * 名称
     * @return String
     */
    protected abstract String name();

    @Override
    public boolean support(Object object){
        if(object == null){
            return false;
        }
        return Objects.equals(object.getClass(), managedClass);
    }

    @Override
    public synchronized void add(T managed) {
        if(managed == null){
            throw new IllegalArgumentException("Add " + name() + " failure, managed is null");
        }
        if(managed.id() == null || "".equals(managed.id())){
            throw new IllegalArgumentException("Param: managed.id() is empty when add " + name());
        }
        if(!support(managed)){
            String errorMsg = TextUtils.format("The {} type '{}' failure are not supported. " +
                            "Current manager support type is '{}'",
                    name(), managed.id(), managedClass.getSimpleName());
            throw new RuntimeException(errorMsg);
        }
        log(managed, "be added");
        managedMap.put(managed.processId(), managed);
    }

    @Override
    public boolean exist(String id) {
        return managedMap.containsKey(id);
    }



    @Override
    public void start(String id) throws Exception{
        T managed = get(id);
        if(managed != null){
            start(managed);
            log(managed, "start success");
        } else {
            String errorMsg = TextUtils.format("Not found {} '{}'", name(), id);
            throw new Exception(errorMsg);
        }
    }

    @Override
    public final void start(T managed) throws Exception {
        add(managed);
        toStart(managed);
    }


    @Override
    public void stop(String id) throws Exception{
        T managed = get(id);
        if(managed != null){
            toStop(managed);
            log(managed, "stop success");
        } else {
            String errorMsg = TextUtils.format("Not found {} '{}'", name(), id);
            throw new Exception(errorMsg);
        }
    }


    @Override
    public synchronized void remove(String id) throws Exception{
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Remove " + name() + " failure, id can't be empty");
        }
        if(managedMap.containsKey(id)){
            String errorMsg = TextUtils.format("Remove {} failure, Not found {} id '{}'",
                    name(), name(), id);
            throw new Exception(errorMsg);
        }
        T managed = get(id);
        if(isStart(managed)){
            toStop(managed);
            managedMap.remove(id);
        } else {
            managedMap.remove(id);
        }
        log(managed, "remove success");
    }


    @Override
    public void startAll(Listener<T> listener) {
        for (T managed : managedMap.values()) {
            if(isStart(managed)){
                logWarn(managed, "have already started when startAll. and ignore");
                continue;
            }
            try {
                start(managed);
                listener.success(managed.id());
            } catch (Exception e) {
                getLogger().error("Start {} id[{}] failure when startAll", name(), managed.id(), e);
                Consumer<T> failure = listener.failure(managed.id(), e);
                if(failure != null){
                    failure.accept(managed);
                }
            }
        }
    }


    @Override
    public void stopAll(Listener<T> listener) {
        for (T managed : managedMap.values()) {
            if(!isStart(managed)){
                logWarn(managed, "not started when stopAll. and ignore");
                continue;
            }
            try {
                toStop(managed);
                listener.success(managed.id());
            } catch (Exception e) {
                logError(managed, "stop failure when stopAll", e);
                Consumer<T> failure = listener.failure(managed.id(), e);
                if(failure != null){
                    failure.accept(managed);
                }
            }
        }
    }

    @Override
    public State getState(String id) {
        if(id == null){
            getLogger().error("Param: id is null when getState");
            return null;
        }
        T managed = get(id);
        if(managed == null){
            getLogger().error("Not found input '{}' when getState", id);
            return null;
        }
        return managed.state();
    }

    @Override
    public T get(String id) {
        if(StringUtils.isEmpty(id)){
            getLogger().error("Param: id is null when get "+ name());
            return null;
        }
        return managedMap.get(id);
    }

    @Override
    public Set<String> getIds() {
        return Collections.unmodifiableSet(managedMap.keySet());
    }

    /**
     * 子类实现启动动作
     * @param managed 被管理者
     * @throws Exception 启动异常
     */
    protected abstract void toStart(T managed) throws Exception;

    /**
     * 子类实现停止的方法
     * @param managed 被管理者
     * @throws Exception 停止异常
     */
    protected abstract void toStop(T managed) throws Exception;



    /**
     * 是否启动。当为 STARTING、RUNNING状态时表示启动
     * @param managed 被管理者
     * @return 启动返回 true. 没有启动返回false
     */
    private boolean isStart(T managed){
        State state = managed.state();
        if(state == State.RUNNING || state == State.STARTING){
            return true;
        } else {
            return false;
        }
    }


    private void log(T managed, String msg){
        getLogger().info("The process '{}' to {}[{}] {}", managed.processId(), name(), managed.id(), msg);
    }

    private void logWarn(T managed, String msg){
        getLogger().warn("The process '{}' to {}[{}] {}", managed.processId(), name(), managed.id(), msg);
    }

    private void logError(T managed, String msg, Exception e){
        getLogger().error("The process '{}' to {}[{}] {} failure. {}", managed.processId(), name(),
                managed.id(), msg, e.getMessage(), e);
    }

}

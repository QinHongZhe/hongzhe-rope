package com.starblues.rope.core.common;

/**
 * 身份标识接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Identity {

    /**
     * 全局唯一id
     * @return String
     */
    String id();

    /**
     * 名称
     * @return String
     */
    String name();

    /**
     * 描述
     * @return String
     */
    String describe();

}

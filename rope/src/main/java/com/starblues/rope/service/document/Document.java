package com.starblues.rope.service.document;

/**
 * 插件文档接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Document {

    String README = "README.md";


    /**
     * md文档名称
     * @return md文档名称
     */
    String name();


    /**
     * md文档访问路径后缀
     * @return md
     */
    String pathSuffix();


}

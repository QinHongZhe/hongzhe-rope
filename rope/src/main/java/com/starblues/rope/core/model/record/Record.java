package com.starblues.rope.core.model.record;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 数据记录接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Record {


    /**
     * 添加列。
     * @param column 列数据
     */
    void putColumn(final Column column);

    /**
     * 得到列集合
     * @return 列数据迭代器
     */
    Collection<Column> getColumns();

    /**
     * 将所有列转换成map集合
     * @return Map
     */
    Map<String, Object> toMap();


    /**
     * 得到列的key集合
     * @return 列数据迭代器
     */
    Set<String> getColumnKeys();

    /**
     * 通过key得到列数据
     * @param key 列的key
     * @return 列数据迭代器
     */
    Column getColumn(String key);


    /**
     * 得到列数量
     * @return 列数据量
     */
    long getColumnNumber();

    /**
     * 得到数据字节数
     * @return 字节数
     */
    long getByteSize();

    /**
     * 是否为空
     * @return 空返回 true. 不为空返回 false
     */
    boolean isEmpty();


    /**
     * 重写 toString
     * @return String
     */
    @Override
    String toString();

}

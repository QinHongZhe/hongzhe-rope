package com.starblues.rope.core.model.record;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 用于标识后一条记录。如果是最后一条记录, 则停止当前流程。
 * 只作用于一次性执行的输入, 也就是如下实现类的输入:
 * @see com.starblues.rope.core.input.support.reader.OneReaderInput
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-13
 */
public class LastRecordSigner implements Record{


    @Override
    public void putColumn(Column column) {

    }

    @Override
    public Collection<Column> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getColumnKeys() {
        return Collections.emptySet();
    }

    @Override
    public Column getColumn(String key) {
        return null;
    }

    @Override
    public long getColumnNumber() {
        return 0;
    }

    @Override
    public long getByteSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}

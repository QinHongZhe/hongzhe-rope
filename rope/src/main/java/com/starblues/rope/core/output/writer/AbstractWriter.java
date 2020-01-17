package com.starblues.rope.core.output.writer;

import com.starblues.rope.core.converter.Converter;
import com.starblues.rope.core.model.record.Record;

/**
 * 抽象的写入者。不可设置转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractWriter implements Writer<Record>{



    public AbstractWriter(){
    }


    @Override
    public final Record convert(Record record) throws Exception {
        return record;
    }

    /**
     * 该子类不可设置转换器, 因此返回false
     * @param converter 数据转换器
     * @return false
     */
    @Override
    public final boolean support(Converter converter) {
        return false;
    }

    @Override
    public Class<Record> convertDataClass() {
        return Record.class;
    }

}

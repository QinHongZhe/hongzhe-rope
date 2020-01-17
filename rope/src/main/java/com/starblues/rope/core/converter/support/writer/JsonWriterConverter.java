package com.starblues.rope.core.converter.support.writer;

import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.converter.AbstractWriterConverter;
import com.starblues.rope.core.model.record.Record;
import org.springframework.stereotype.Component;

/**
 * json 数据写入转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class JsonWriterConverter extends AbstractWriterConverter<String> {

    private static String ID = "columns-to-json";

    private final Gson gson;


    public JsonWriterConverter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void initialize(String processId) throws Exception {
        // 不需要初始化
    }

    @Override
    public String convert(Record record) {
        return gson.toJson(record.toMap());
    }

    @Override
    public ConfigParameter configParameter() {
        return null;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return "Record 记录列集合转 json";
    }
}

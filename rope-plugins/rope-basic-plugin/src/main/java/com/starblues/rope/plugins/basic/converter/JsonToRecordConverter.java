package com.starblues.rope.plugins.basic.converter;

import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.utils.PluginLogger;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 一级 json 字符串转换Record
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
@Component
public class JsonToRecordConverter extends AbstractInputConverter<String> {

    private final static String ID = "json-to-record";

    private final Gson gson;

    private Logger logger;

    public JsonToRecordConverter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void initialize(String processId) throws Exception {
        logger = PluginLogger.getLogger(this, processId);
    }

    @Override
    public RecordGroup convert(String s) {
        if(StringUtils.isEmpty(s)){
            return null;
        }
        try {
            RecordGroup recordGroup = new RecordGroup();
            if(s.startsWith("[") && s.endsWith("]")){
                // 说明为数组
                List<Map<String, Object>> listMap = gson.fromJson(s, List.class);
                if(listMap == null || listMap.isEmpty()){
                    return null;
                }
                for (Map<String, Object> stringObjectMap : listMap) {
                    Record record = getRecord(stringObjectMap);
                    if(record != null){
                        recordGroup.addRecord(record);
                    }
                }

            } else {
                Map<String, Object> map = gson.fromJson(s, Map.class);
                Record record = getRecord(map);
                if(record != null){
                    recordGroup.addRecord(record);
                }
            }
            return recordGroup;
        } catch (Exception e){
            logger.error("Error converting JSON string. {}", s, e);
            return null;
        }
    }

    private Record getRecord(Map<String, Object> map){
        if(map == null || map.isEmpty()){
            return null;
        }
        Record record = new DefaultRecord();
        map.forEach((k, v)->{
            record.putColumn(Column.auto(k, v));
        });
        return record;
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
        return "输入json转换器";
    }

    @Override
    public String describe() {
        return "将输入的一级json字符串, 转换成 Record bean";
    }
}

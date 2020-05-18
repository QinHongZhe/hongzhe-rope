package com.starblues.rope.core.model;

import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.LastRecordSigner;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import lombok.Data;

/**
 * 记录包装者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class RecordWrapper {

    /**
     * 流程id
     */
    private String processId;

    /**
     * 数据记录组
     */
    private RecordGroup recordGroup;

    /**
     * 记录类型
     */
    private Class<? extends Record> recordType;


    /**
     * 是否为用户数据的记录
     * @return 是用户记录的数据返回true, 不是用户记录的数据返回false
     */
    public boolean isUserRecord(){
        return recordType == DefaultRecord.class;
    }

    /**
     * 是否为最后一条记录的标记者
     * @return 是返回ture, 不是返回false
     */
    public boolean isLastRecordSigner(){
        return recordType == LastRecordSigner.class;
    }


}

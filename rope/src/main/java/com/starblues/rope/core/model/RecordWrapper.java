package com.starblues.rope.core.model;

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


    private String processId;
    private RecordGroup recordGroup;


}

package com.starblues.rope.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starblues.rope.system.initializers.support.migration.suport.BaseMigration;
import lombok.Data;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@TableName(value = "process_info", schema = BaseMigration.SCHEMA)
public class ProcessInfo {

    /**
     * id
     */
    @TableId(type = IdType.INPUT)
    private String processId;


    private String processJson;

}

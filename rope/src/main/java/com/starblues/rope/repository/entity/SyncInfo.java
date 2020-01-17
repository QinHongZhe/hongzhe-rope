package com.starblues.rope.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starblues.rope.system.initializers.support.migration.suport.BaseMigration;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统信息
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@TableName(value = "sync_info", schema = BaseMigration.SCHEMA)
public class SyncInfo implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 同步值
     */
    private String value;

}

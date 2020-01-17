package com.starblues.rope.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starblues.rope.system.initializers.support.migration.suport.BaseMigration;
import lombok.Data;

/**
 * 用户实体bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@TableName(value = "user", schema = BaseMigration.SCHEMA)
public class User {

    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    /**
     * 名称
     */
    private String name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户加盐
     */
    private String salt;

    /**
     * hash 迭代次数
     */
    private Integer hashIterations;


    /**
     * 过期时间戳
     */
    private Long ttlMillis;

}

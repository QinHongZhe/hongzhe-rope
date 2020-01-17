package com.starblues.rope.rest.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * rest接口返回的数据模型
 *
 * @author zhangzhuo
 * @version 1.0
 */
@ApiModel(value="输出模型", description="输出模型包装")
public class Result<T> implements Serializable{

    /**
     * 返回码
     */
    @ApiModelProperty(value = "返回code码, 约定 1 成功, 0 失败", required = true)
    private Integer code;

    /**
     * 提示信息
     */
    @ApiModelProperty(value = "提示信息", required = true)
    private String msg;

    /**
     * 返回的数据
     */
    @ApiModelProperty(value = "输出数据体" , required = true)
    private T data;

    public Result() {
    }

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> Builder<T> toBuilder(){
        return new Builder<T>();
    }

    public static class Builder<T>{

        private Integer code;

        private String msg;

        private T data;

        private com.starblues.rope.rest.common.ResponseEnum response;

        public Builder<T> code(Integer code){
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg){
            this.msg = msg;
            return this;
        }

        public Builder<T> data(T data){
            this.data = data;
            return this;
        }

        public Builder<T> responseEnum(com.starblues.rope.rest.common.ResponseEnum response){
            if (this.code == null) {
                this.code = response.getCode();
            }
            if(this.msg == null){
                this.msg = response.getMessage();
            }
            return this;
        }

        public Result<T> builder(){
            return new Result<>(code, msg, data);
        }
    }


    public enum ResponseEnum implements com.starblues.rope.rest.common.ResponseEnum {

        // 响应成功
        SUCCESS(1, "成功"),
        // 响应失败
        ERROR(0, "失败"),

        // 创建资源成功
        CREATE_SUCCESS(1, "创建成功"),
        // 创建资源失败
        CREATE_ERROR(0, "创建失败"),
        // 创建资源失败，主键冲突
        CREATE_ERROR_DUPLICATE_ID(0, "创建失败,主键已经存在"),
        // 创建资源失败，传入的数据为空
        CREATE_ERROR_DATA_IS_EMPTY(0, "创建失败,传入的数据为空"),
        // 创建资源失败，参数不合法
        CREATE_ERROR_ILLEGAL_PARAMS(0, "创建失败,参数不合法"),
        //创建成功，但是添加Job任务失败
        CREATE_SUCCESS_BUT_JOB_ERROR(0, "创建成功，但是添加Job任务失败"),

        // 删除资源成功
        DELETE_SUCCESS(1, "删除成功"),
        // 删除资源失败
        DELETE_ERROR(0, "删除失败"),
        // 删除资源失败
        DELETE_ERROR_DATA_IS_NOT_EXISTED(0, "删除失败,要删除的数据不存在"),
        //从Job任务中删除失败
        DELETE_ERROR_FROM_JOB(0, "从Job任务中删除失败"),

        // 更新资源成功
        UPDATE_SUCCESS(1, "更新成功"),
        // 更新资源失败
        UPDATE_ERROR(0, "更新失败"),
        // 更新资源失败，传入的数据为空
        UPDATE_ERROR_DATA_IS_EMPTY(0, "更新失败,传入的数据为空"),
        // 更新资源失败，参数不合法
        UPDATE_ERROR_ILLEGAL_PARAMS(0, "更新失败,参数不合法"),

        //更新成功，但是添加Job任务失败
        UPDATE_SUCCESS_BUT_JOB_ERROR(0, "更新成功，但是添加Job任务失败"),

        // 获取资源成功
        GET_SUCCESS(1, "获取成功"),
        // 获取资源失败
        GET_ERROR(0, "获取失败"),
        // 获取资源失败
        GET_ERROR_ILLEGAL_QUERY_PARAMS(0, "获取失败,请求参数不合法"),


        // 操作成功
        OPERATE_SUCCESS(1, "操作成功"),
        // 操作失败
        OPERATE_ERROR(0, "操作失败"),

        ;

        private Integer code;

        private String message;

        ResponseEnum(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

}

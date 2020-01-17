package com.starblues.rope.service.process;

import com.starblues.rope.core.common.param.ConfigParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流程参数Service接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ProcessParamService {

    /**
     * 得到全部输入的参数信息
     * @return 配置的参数信息集合
     */
    List<ParamInfo> getInputConfigParam();

    /**
     * 得到全部输出的参数信息
     * @return 配置的参数信息集合
     */
    List<ParamInfo> getOutputConfigParam();

    /**
     * 得到写入者的参数信息
     * @return 配置的参数信息集合
     */
    List<ParamInfo> getWriterConfigParam();


    /**
     * 得到读取者的参数信息
     * @return 配置的参数信息集合
     */
    List<ParamInfo> getReaderConfigParam();


    /**
     * 得到数据处理器的参数信息
     * @return 配置的参数信息集合
     */
    List<ParamInfo> getDataHandlerConfigParam();

    /**
     * 得到输入数据转换器的参数信息
     * @return 配置的参数信息集合
     */
    List<ConverterParamInfo> getInputConverterConfigParam();

    /**
     * 得到输出数据转换器的参数信息
     * @return 配置的参数信息集合
     */
    List<ConverterParamInfo> getWriterConverterConfigParam();


    @Data
    @Api(value = "参数信息",description = "参数的详细信息")
    class ParamInfo{

        @ApiModelProperty(value = "模块id" , required = true)
        private String id;

        @ApiModelProperty(value = "模块名称", required = true)
        private String name;

        @ApiModelProperty(value = "对模块的描述", required = true)
        private String description;

        @ApiModelProperty(value = "模块的类型", required = true)
        private String type;

        @ApiModelProperty(value = "该模块的所需的参数信息" , required = true)
        private ConfigParam configParam;
    }

    @Data
    @Api(value = "转换器的配置",description = "转换器的配置信息")
    class ConverterParamInfo extends ParamInfo{
        /**
         * 转换器转换的类型
         */
        @ApiModelProperty(value = "如果是输入转换器, 则为支持的输入类型; " +
                "如果是输出转换器, 则为支持的输处类型" , required = true)
        private Class<?> convertClass;
    }

}

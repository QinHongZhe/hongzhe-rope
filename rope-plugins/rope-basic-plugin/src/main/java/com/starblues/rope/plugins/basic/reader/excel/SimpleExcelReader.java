package com.starblues.rope.plugins.basic.reader.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.ListMapField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.reader.BaseReaderConfigParameter;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.ParamUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * 简单的excel读取者
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-19
 */
@Component
public class SimpleExcelReader implements Reader {

    private final static String ID = "simple-excel";

    private final Param param = new Param();

    private File excelFile;


    @Override
    public void initialize(String processId) throws Exception {
        String excelFilePath = param.getExcelFilePath();
        ParamUtils.check(processId, "excelFilePath", excelFilePath);
        excelFile = new File(excelFilePath);
        if(!excelFile.exists()){
            throw new FileNotFoundException("Excel file[" + excelFilePath + "] not exist");
        }
    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        EasyExcel.read(excelFile, new AnalysisExcel(consumer))
                .sheet()
                .sheetNo(param.getSheetNo() - 1)
                .headRowNumber(param.getHeadRowNumber())
                .doRead();
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public BaseReaderConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "excel简单的读取";
    }

    @Override
    public String describe() {
        return "读取简单的excel中的数据";
    }


    private class AnalysisExcel extends AnalysisEventListener<Map<Integer, Object>> {


        private final Consumer consumer;

        private Map<Integer, String> headMap;

        /**
         * 表头计数器, 用于获取数据映射的表头
         */
        private Integer headerCount = 1;




        private AnalysisExcel(Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            // 读取的第一条数据为表头
            if(headerCount == param.getHeadRowNumber()){
                // 为表头
                this.headMap = headMap;
            }
            headerCount++;
        }

        @Override
        public void invoke(Map<Integer, Object> dataMap, AnalysisContext analysisContext) {
            if(dataMap == null || dataMap.isEmpty()){
                return;
            }

            Record record = DefaultRecord.instance();

            dataMap.forEach((number, value)->{
                // 获取该数据列的表头值
                String headString = null;
                if(headMap != null && !headMap.isEmpty()){
                    // 表头数据不为空
                    headString = headMap.get(number);
                }
                if(StringUtils.isEmpty(headString)){
                    // 表头为空
                    headString = String.valueOf(number);
                }


                // 表头映射的定义
                Map<String, String> headMappings = param.getHeadMappings();
                if(headMappings != null && !headMappings.isEmpty()){
                    // 表头映射器不为空
                    String mappingHeaderString = headMappings.get(headString);
                    if(!StringUtils.isEmpty(mappingHeaderString)){
                        // 获取到的表头不为空
                        headString = mappingHeaderString;
                    }
                }

                record.putColumn(Column.auto(headString, value));
            });

            consumer.accept(record);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        }
    }


    @Getter
    public static class Param extends BaseReaderConfigParameter{

        private static final String P_HEAD_MAPPINGS = "headMappings";
        private static final String P_EXCEL_HEAD_VALUE = "excelHeadValue";
        private static final String P_MAPPING_VALUE = "mappingValue";

        private static final String P_EXCEL_FILE_PATH = "excelFilePath";
        private static final String P_SHEET_NO = "sheetNo";
        private static final String P_HEAD_ROW_NUMBER = "headRowNumber";


        /**
         * excel文件路径
         */
        private String excelFilePath;

        /**
         * sheet编号。从1开始. 必须大于等于1
         */
        private int sheetNo = 1;

        /**
         * 与数据对应的表头行数。从1开始. 必须大于等于1
         */
        private int headRowNumber = 1;

        /**
         * 表头与字段Key映射关系.key 为excel表头的值, value为该表头映射的值
         */
        private Map<String, String> headMappings = null;


        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            excelFilePath = paramInfo.getString(P_EXCEL_FILE_PATH);

            sheetNo = paramInfo.getInt(P_SHEET_NO, sheetNo);
            if(sheetNo < 1){
                sheetNo = 1;
            }

            headRowNumber = paramInfo.getInt(P_HEAD_ROW_NUMBER, headRowNumber);
            if(headRowNumber < 1){
                headRowNumber = 1;
            }

            headMappings = paramInfo.mapping(P_HEAD_MAPPINGS, P_EXCEL_HEAD_VALUE, P_MAPPING_VALUE);
        }

        @Override
        protected void configParam(ConfigParam configParam) {

            configParam.addField(
                    TextField.toBuilder(
                            P_EXCEL_FILE_PATH, "excel路径", "")
                            .attribute(TextField.Attribute.TEXTAREA)
                            .description("excel文件路径")
                            .required(true)
                            .build()
            );


            configParam.addField(
                    NumberField.toBuilder(
                            P_SHEET_NO, "sheet编号", sheetNo)
                            .description("sheet编号, 编号从1开始。默认为：1")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(false)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            P_HEAD_ROW_NUMBER, "表头行数", headRowNumber)
                            .description("表头行数, 行数从1开始。默认为：1")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(false)
                            .build()
            );

            configParam.addField(
                    ListMapField.toBuilder(P_HEAD_MAPPINGS, "表头映射", P_EXCEL_HEAD_VALUE, P_MAPPING_VALUE)
                            .description("将表头映射成定义的值。主要用于将表头映射为程序所处理的key, 如果没有定义, 默认使用表头值。")
                            .keyValueDescription("excel表头", "要映射的表头")
                            .required(false)
                            .build()
            );
        }
    }


}

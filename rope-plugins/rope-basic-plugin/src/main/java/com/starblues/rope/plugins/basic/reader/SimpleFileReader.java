package com.starblues.rope.plugins.basic.reader;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.BaseReaderConfigParameter;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 简单的文件读取者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SimpleFileReader implements Reader {

    public static final String ID = "simple-file";

    private final Param param;

    private Path filePath;
    private AbstractInputConverter inputConverter;

    private final ConverterFactory converterFactory;

    public SimpleFileReader(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
        this.param = new Param();
    }

    @Override
    public void initialize(String processId) throws Exception {
        Path path = Paths.get(param.getFilePath());
        if(Files.exists(path)){
            filePath = path;
        } else {
            throw new IOException("Not found this file : " + param.getFilePath());
        }
        inputConverter = converterFactory.getInputConverter(processId, String.class);
    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        List<String> lines = Files.readAllLines(filePath, Charset.forName(param.getCharset()));
        RecordGroup recordGroup = new RecordGroup();
        if(inputConverter != null){
            for (String line : lines) {
                recordGroup.addRecordGroup(inputConverter.convert(line));
            }
        } else {
            for (int i = 0; i < lines.size(); i++) {
                Record record = new DefaultRecord();
                record.putColumn(Column.auto(String.valueOf(i), lines.get(i)));
                recordGroup.addRecord(record);
            }
        }
        if(param.getClean() && lines.size() > 0){
            Files.write(filePath, "".getBytes());
        }
        consumer.accept(recordGroup);
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
        return "简单的文件读取者";
    }

    @Override
    public String describe() {
        return "简单的文件读取者";
    }

    @Getter
    public static class Param extends BaseReaderConfigParameter{

        public static final String FILE_PATH = "filePath";
        public static final String CHARSET = "charset";
        public static final String CLEAN = "clean";


        private String filePath;
        private String charset = "utf-8";
        private Boolean clean = false;



        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            this.filePath = configParamInfo.getString(FILE_PATH);
            this.charset = configParamInfo.getString(CHARSET, StandardCharsets.UTF_8.toString());
            this.clean = configParamInfo.getBoolean(CLEAN, false);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            configParam.addField(
                    TextField.toBuilder(
                            FILE_PATH, "文件路径", "")
                            .description("当前系统的文件绝对路径")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(
                            CHARSET, "字符编码", StandardCharsets.UTF_8.toString())
                            .description("读文件的字符编码")
                            .required(true)
                            .build()
            );


            configParam.addField(
                    BooleanField.toBuilder(
                            CLEAN, "是否清除文件", false)
                            .description("读取文件后是否清除文件")
                            .required(true)
                            .build()
            );

        }
    }

}

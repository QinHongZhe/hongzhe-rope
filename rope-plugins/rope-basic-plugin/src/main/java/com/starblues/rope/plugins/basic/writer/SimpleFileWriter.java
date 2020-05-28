package com.starblues.rope.plugins.basic.writer;

import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.writer.AbstractConverterWriter;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.plugins.basic.utils.FileUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 简单的文件写入者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SimpleFileWriter extends AbstractConverterWriter<String> {

    public static final String ID = "simple-file";

    private Path existJsonPath;

    private final Param param;
    private final Gson gson;

    public SimpleFileWriter() {
        this.gson = new Gson();
        this.param = new Param();
    }

    @Override
    protected String customConvert(Record record) throws Exception {
        return gson.toJson(record);
    }


    @Override
    public void initialize(String processId) throws Exception {
        this.existJsonPath = FileUtils.getExistFile(param.getFilePath());
    }

    @Override
    public void write(List<String> strings) throws Exception {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(existJsonPath, StandardOpenOption.APPEND);
        for (String record : strings) {
            write(bufferedWriter, record);
        }
        bufferedWriter.close();
    }


    private void write(BufferedWriter bufferedWriter, String data) throws IOException {
        bufferedWriter.write(data);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    @Override
    public void destroy() throws Exception{

    }

    @Override
    public BaseWriterConfigParameter configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "简单的文件写入者";
    }

    @Override
    public String describe() {
        return "将数据记录写入到文件中。如果配置了转换器, 则输出转换器输出的内容, 如果没有设置转换器, 则以 json 格式输出";
    }


    @Getter
    public static class Param extends BaseWriterConfigParameter{

        public static final String FILE_PATH = "filePath";

        private String filePath;


        @Override
        protected void childParsing(ConfigParamInfo configParamInfo) {
            this.filePath = configParamInfo.getString(FILE_PATH);
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
        }

    }

}

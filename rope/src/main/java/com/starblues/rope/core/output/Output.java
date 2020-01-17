package com.starblues.rope.core.output;

import com.starblues.rope.core.common.manager.Managed;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.output.writer.Writer;
import lombok.Data;

import java.util.List;

/**
 * 数据的输出接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Output extends Managed {

    /**
     * 添加数据写入者
     * @param writerWrapper 数据写入者包装对象
     */
    void addWriter(WriterWrapper writerWrapper);

    /**
     * 添加数据写入者
     * @param writerWrappers 数据写入者包装对象集合
     */
    void addWriter(List<WriterWrapper> writerWrappers);

    /**
     * 得到数据写入者
     * @return 数据写入者包装对象集合
     */
    List<WriterWrapper> getWriter();

    /**
     * 启动
     * @throws Exception 启动异常
     */
    void start() throws Exception;

    /**
     * 停止输入
     * @throws Exception 停止异常
     */
    void stop() throws Exception;

    /**
     * 输出数据
     * @param recordWrapper 数据b包装者
     */
    void output(RecordWrapper recordWrapper);

    /**
     * 写入者包装对象
     */
    @Data
    class WriterWrapper{
        private String code;
        private Writer writer;
    }

}

package com.starblues.rope.core.input;

import com.starblues.rope.core.input.reader.Reader;

/**
 * 主动读取型数据的输入接口。携带 Reader
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ReaderInput extends Input{


    /**
     * 设置数据读取者
     * @param reader 数据读取者
     */
    void setReader(Reader reader);



    /**
     * 得到数据读取者
     * @return 数据读取者
     */
    Reader getReader();

}

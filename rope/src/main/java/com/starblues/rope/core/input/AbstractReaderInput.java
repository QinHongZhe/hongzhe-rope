package com.starblues.rope.core.input;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.reader.DoNotOperateReader;
import com.starblues.rope.core.input.reader.Reader;


/**
 * 抽象的主动读取型数据的输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractReaderInput extends AbstractInput
        implements ReaderInput {

    private Reader reader;
    private Consumer consumer;

    /**
     * 设置数据读取者
     * @param reader 数据读取者
     */
    @Override
    public final void setReader(Reader reader){
        if(state() == State.RUNNING){
            throw new RuntimeException("Cannot set reader. Because the input is already running");
        }
        if(reader != null){
            this.reader = reader;
        }
    }

    @Override
    public final Reader getReader() {
        return new DoNotOperateReader(reader);
    }


    @Override
    protected void startBefore(Consumer consumer) throws Exception {
        if(reader == null){
            throw new NullPointerException("Reader can't be null");
        }
        this.consumer = consumer;
    }


    @Override
    protected void toStop() throws Exception {
        reader.destroy();
    }

    /**
     * 读取记录的调用
     */
    public synchronized void readRecord(){
        if(state() != State.RUNNING || getTransport() == null){
            throw new RuntimeException("process '" + processId() + "' not run this input '" + id() + "'," +
                    "this input is not running, state is " + state());
        }
        try {
            this.reader.reader(this.consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

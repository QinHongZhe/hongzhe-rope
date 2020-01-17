package com.starblues.rope.exception;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 异常组
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ExceptionGroup extends Exception{

    private List<Exception> exceptionList = Lists.newArrayList();
    private List<String> exceptionMessage = Lists.newArrayList();

    public void addException(Exception e){
        if(e != null){
            exceptionList.add(e);
            exceptionMessage.add(e.getMessage());
        }
    }

    public void addException(Exception e, String message){
        if(e != null){
            exceptionList.add(e);
            exceptionMessage.add(message);
        }

    }

    public boolean isHaveException(){
        if(exceptionList.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public ExceptionGroup() {
        super();
    }


    @Override
    public String getMessage() {
        return Joiner.on(";")
                .skipNulls()
                .join(exceptionMessage);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}

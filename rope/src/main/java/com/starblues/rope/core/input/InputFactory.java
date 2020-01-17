package com.starblues.rope.core.input;

import com.gitee.starblues.integration.user.PluginUser;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.input.support.accept.AbstractAcceptInput;
import com.starblues.rope.core.common.config.CommonConfig;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.utils.CommonUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.starblues.rope.utils.CommonUtils.getImpls;

/**
 * 输入工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class InputFactory {


    private final PluginUser pluginUser;

    public InputFactory(PluginUser pluginUser) {
        this.pluginUser = pluginUser;
    }

    /**
     * 得到输入
     * @param processId 流程id
     * @param inputConfig 输入的配置
     * @return 输出
     * @throws Exception 获取输入的异常
     */
    public Input getInput(String processId,
                          ProcessConfig.InputConfig inputConfig) throws Exception {
        if(StringUtils.isEmpty(processId)){
            throw new IllegalArgumentException("ProcessInfo id can't be null");
        }
        if(inputConfig == null){
            throw new NullPointerException("ProcessInfo input config can't be null");
        }
        String inputId = inputConfig.getId();
        if(StringUtils.isEmpty(inputId)){
            throw new IllegalArgumentException("Input id can't be empty");
        }



        // 查找系统中读取型输入
        AbstractReaderInput abstractReaderInput = getImpls(pluginUser, AbstractReaderInput.class, readerInput -> {
            return Objects.equals(readerInput.id(), inputId);
        });

        Input input = null;

        if(abstractReaderInput != null){
            abstractReaderInput = pluginUser.generateNewInstance(abstractReaderInput);
            abstractReaderInput.setProcessId(processId);
            input = abstractReaderInput;
        } else {
            // 查找系统中满足接受型的输入
            AbstractAcceptInput abstractAcceptInput = getImpls(pluginUser, AbstractAcceptInput.class,
                    acceptInput -> {
                        return Objects.equals(acceptInput.id(), inputId);
                    });
            if(abstractAcceptInput != null){
                // 生成一个新的接受性的输入，保证每个输入都是独自的实例
                abstractAcceptInput = pluginUser.generateNewInstance(abstractAcceptInput);
                abstractAcceptInput.setProcessId(processId);
                input = abstractAcceptInput;
            } else {
                throw new Exception("Not found input '" + inputId + "'");
            }
        }


        ConfigParameter inputConfigParameter = input.configParameter();
        if(inputConfigParameter != null){
            CommonUtils.parsingConfig(inputConfigParameter, inputConfig.getParams());
        }

       if(input instanceof ReaderInput){
           // 初始化 ReaderInput 型输入
           initReaderInput(processId, inputConfig, input);
        }

        // 初始化输入
        input.initialize();
        return input;
    }



    private void initReaderInput(String processId,
                                 ProcessConfig.InputConfig inputConfig,
                                 Input input) throws Exception {
        CommonConfig readerConfig = inputConfig.getReader();
        if(readerConfig == null){
            throw new NullPointerException("ProcessInfo input reader config can't be null");
        }
        String readerId = readerConfig.getId();
        if(StringUtils.isEmpty(readerId)){
            throw new IllegalArgumentException("Reader id can't be empty");
        }

        Reader reader = getImpls(pluginUser, Reader.class, impl -> {
            return Objects.equals(impl.id(), readerId);
        });
        if(reader == null){
            throw new Exception("Not found reader '" + readerId + "'");
        }
        // 生成一个全新的读取者。则多个流程会公用同一个数据读取者。会导致数据错误。
        reader = pluginUser.generateNewInstance(reader);

        // 如果该输入为主动读取型数据的输入, 则给输入设置数据读取者
        if(input instanceof ReaderInput){
            ((ReaderInput)input).setReader(reader);
        }
        // 初始化数据读取者
        ConfigParameter readerConfigParameter = reader.configParameter();
        CommonUtils.parsingConfig(readerConfigParameter, readerConfig.getParams());
        reader.initialize(processId);
    }


}

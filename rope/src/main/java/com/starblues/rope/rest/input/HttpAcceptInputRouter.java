package com.starblues.rope.rest.input;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.support.accept.AbstractHttpAcceptInput;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.starblues.rope.utils.CommonUtils.getImpls;

/**
 * http 接受型输入
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
@RestController
@RequestMapping("/input")
@Slf4j
public class HttpAcceptInputRouter extends BaseResource {

    private final ProcessFactory processFactory;

    public HttpAcceptInputRouter(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }


    @PostMapping(value = "/json/{processId}", produces = "application/json;charset=UTF-8")
    public Result<String> jsonAccept(@PathVariable("processId") String processId,
                                     HttpServletRequest request){

        ProcessManager processManager = processFactory.getProcessManager(processId);
        try {
            Input input = processManager.getInput();
            if(input instanceof AbstractHttpAcceptInput){
                AbstractHttpAcceptInput abstractHttpAcceptInput = (AbstractHttpAcceptInput) input;
                // 获取输入流
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));

                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = streamReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                abstractHttpAcceptInput.consumeMessage(stringBuilder.toString());
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "没有发现流程 '" + processId + "' 的数据接受者");
            }
        } catch (Exception e){
            log.error("Http accept json data error.  processId=[{}]", processId, e);
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
        }


    }


}

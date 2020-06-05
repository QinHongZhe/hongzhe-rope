package com.starblues.rope.rest.input;

import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.input.support.accept.AbstractHttpAcceptInput;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * http 接受型输入
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
@RestController
@RequestMapping(HttpAcceptInputRouter.PATH)
@Slf4j
public class HttpAcceptInputRouter extends BaseResource {


    public static final String PATH = "/input";
    public static final String BODY_ACCEPT_PATH = "/body";

    private final ProcessFactory processFactory;

    public HttpAcceptInputRouter(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }


    /**
     * body 体里面接受数据
     * @param processId 流程id
     * @param request 请求的 HttpServletRequest
     * @return Result
     */
    @PostMapping(value = HttpAcceptInputRouter.BODY_ACCEPT_PATH + "/{processId}")
    public Result<String> jsonAccept(@PathVariable("processId") String processId,
                                     HttpServletRequest request){
        return consumeData(processId, ()->{
            try {
                // 获取输入流
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = streamReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (Exception e){
                return e;
            }
        });
    }


    /**
     * 消费数据
     * @param processId 流程id
     * @param dataSupplier 数据提供者
     * @return 响应结果
     */
    private Result<String> consumeData(String processId, Supplier<Object> dataSupplier){
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager == null){
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, "没有发现流程 '" + processId + "'");
        }
        try {
            Input input = processManager.getInput();

            if(input instanceof AbstractHttpAcceptInput){
                AbstractHttpAcceptInput abstractHttpAcceptInput = (AbstractHttpAcceptInput) input;

                Object o = dataSupplier.get();
                if(o instanceof Throwable){
                    // 出现异常
                    Throwable e = (Throwable) o;
                    log.error("Http supplier data error.  processId=[{}]", processId, e);
                    return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
                }
                abstractHttpAcceptInput.consumeMessage(o);
                return responseBody(Result.ResponseEnum.OPERATE_SUCCESS);
            } else {
                return responseBody(Result.ResponseEnum.OPERATE_ERROR, "没有发现流程 '" + processId + "' 的数据接受者");
            }
        } catch (Exception e){
            log.error("Http accept data error.  processId=[{}]", processId, e);
            return responseBody(Result.ResponseEnum.OPERATE_ERROR, e.getMessage());
        }

    }



}

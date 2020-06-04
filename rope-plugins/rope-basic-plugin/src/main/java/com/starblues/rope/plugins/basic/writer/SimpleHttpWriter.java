package com.starblues.rope.plugins.basic.writer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.DropdownField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.writer.AbstractWriter;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.utils.ParamUtils;
import com.starblues.rope.utils.PluginLogger;
import lombok.Getter;
import okhttp3.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的 http 接口写入者。不带认证. 需要配合数据转换器使用
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
@Component
public class SimpleHttpWriter extends AbstractWriter {

    private final static String ID = "simple-http";

    private Logger logger;

    private OkHttpClient okHttpClient;

    private Param param = new Param();

    private final Gson gson;

    AtomicInteger integer = new AtomicInteger(0);

    public SimpleHttpWriter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void initialize(String processId) throws Exception {
        this.logger = PluginLogger.getLogger(this, processId);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(param.getConnectTimeout(), TimeUnit.SECONDS);

        this.okHttpClient = builder.build();
        ParamUtils.check(processId, "url", param.getUrl());
        ParamUtils.check(processId, "method", param.getMethod());
    }

    @Override
    public void write(List<Record> records) throws Exception {

        String url = param.getUrl();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        List<Map<String, Object>> jsonMap = Lists.newArrayListWithCapacity(records.size());
        for (Record record : records) {
            jsonMap.add(record.toMap());
        }

        String json = gson.toJson(jsonMap);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        requestBuilder = requestBuilder.method(param.getMethod(), requestBody);
        Call call = okHttpClient.newCall(requestBuilder.build());

        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法

            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("Request url '{}' failure, {}", url, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(response.isSuccessful()){
                    logger.debug("Request url '{}' response result ：{}", url, result);
                } else {
                    logger.error("Request url '{}' failure, response code ：{}, response result ：{}", url, response.code(), result);
                }

            }
        });
    }



    @Override
    public void destroy() throws Exception {

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
        return "简单的http";
    }

    @Override
    public String describe() {
        return "简单的http发生者";
    }


    @Getter
    private static class Param extends BaseWriterConfigParameter{

        public static final String P_URL = "url";
        public static final String P_METHOD = "method";
        public static final String P_CONNECT_TIMEOUT = "connectTimeout";

        public static final String METHOD_POST = "POST";
        public static final String METHOD_PUT = "PUT";


        private static final Map<String, String> METHODS = ImmutableMap.of(
                METHOD_POST, METHOD_POST,
                METHOD_PUT, METHOD_PUT);

        /**
         * url
         */
        private String url;

        /**
         * 请求方法
         */
        private String method = METHOD_POST;

        /**
         * 链接超时. 默认10秒
         */
        private int connectTimeout = 10;


        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            url = paramInfo.getString(P_URL);
            method = paramInfo.getString(P_METHOD, METHOD_POST);
            connectTimeout = paramInfo.getInt(P_CONNECT_TIMEOUT, connectTimeout);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            configParam.addField(
                    TextField.toBuilder(
                            P_URL, "url", "")
                            .description("http链接")
                            .required(true)
                            .build()
            );

            configParam.addField(
                    DropdownField.toBuilder(P_METHOD, "请求方法", METHOD_POST, METHODS)
                            .required(true)
                            .description("http请求方法")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(
                            P_CONNECT_TIMEOUT, "连接超时", connectTimeout)
                            .description("连接超时。单位秒")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .required(false)
                            .build()
            );



        }
    }

}

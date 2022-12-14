package com.starblues.rope.plugins.basic.writer;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.writer.AbstractConverterWriter;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.utils.ParamUtils;
import com.starblues.rope.utils.PluginLogger;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * kafka 写入者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class KafkaWriter extends AbstractConverterWriter<String> {

    private Logger log;

    private final static String ID = "kafka_2.12";
    private final Gson gson;



    private KafkaProducer<String, String> producer;
    private Param param;

    public KafkaWriter(Gson gson){
        this.gson = gson;
        param = new Param();
    }


    @Override
    protected String customConvert(Record record) throws Exception {
        return gson.toJson(record);
    }

    @Override
    public void initialize(String processId) throws Exception {
        log = PluginLogger.getLogger(this, processId);
        this.producer = new KafkaProducer<String, String>(param.getProps());
    }

    @Override
    public void write(List<String> strings) throws Exception {
        if(strings == null || strings.isEmpty()){
            return;
        }
        String topic = param.getTopic();
        for (String string : strings) {
            producer.send(new ProducerRecord<>(topic, string), (recordMetaData, e) -> {
                if (e != null) {
                    log.error("Error during publish to topic {} ", topic, e);
                } else {
                    log.debug("Send message {} to topic {} partition {} with offset {}", string, topic,
                            recordMetaData.partition(), recordMetaData.offset());
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        if(producer != null){
            producer.close();
        }
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
        return "kafka写入者";
    }

    @Override
    public String describe() {
        return "kafka 2.12 版本写入者";
    }


    @Getter
    public static class Param extends BaseWriterConfigParameter{

        private static final String P_TOPIC = "topic";
        private static final String P_CK_SERVERS = "servers";
        private static final String P_CK_BATCH_SIZE = "batchSize";
        private static final String P_CK_LINGER_MS = "lingerMs";
        private static final String P_CK_BUFFER_MEMORY = "bufferMemory";

        private String topic;
        private final Map<String, Object> props = Maps.newHashMap();


        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            topic = ParamUtils.check(P_TOPIC, paramInfo.getString(P_TOPIC));

            String servers = ParamUtils.check(P_CK_SERVERS,
                    paramInfo.getString(P_CK_SERVERS));

            props.put(BOOTSTRAP_SERVERS_CONFIG, servers);
            props.put(ACKS_CONFIG, paramInfo.getString(ACKS_CONFIG));
            props.put(RETRIES_CONFIG, paramInfo.getInt(RETRIES_CONFIG));
            props.put(BATCH_SIZE_CONFIG, paramInfo.getInt(P_CK_BATCH_SIZE));
            props.put(LINGER_MS_CONFIG, paramInfo.getInt(P_CK_LINGER_MS));
            props.put(BUFFER_MEMORY_CONFIG, paramInfo.getInt(P_CK_BUFFER_MEMORY));
            props.put(KEY_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put(VALUE_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
        }

        @Override
        protected void configParam(ConfigParam configParam) {

            configParam.addField(
                    TextField.toBuilder(P_CK_SERVERS, "代理服务器", "localhost:9092")
                            .required(true)
                            .description("用于建立与Kafka集群的初始连接的列表主机/端口")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(P_TOPIC, "主题", "")
                            .required(true)
                            .description("发送数据的主题名称")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(RETRIES_CONFIG, "重试次数", 0)
                            .required(true)
                            .description("如果请求失败，可以自动重试的次数")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(P_CK_BATCH_SIZE, "批量大小", 16384)
                            .required(true)
                            .description("批量处理数据的大小.单位: B")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );


            configParam.addField(
                    NumberField.toBuilder(P_CK_LINGER_MS, "处理数据延迟", 1)
                            .required(true)
                            .description("设置批量处理数据的延迟，单位：ms")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );


            configParam.addField(
                    NumberField.toBuilder(P_CK_BUFFER_MEMORY, "缓存的大小", 33554432)
                            .required(true)
                            .description("设置内存缓冲区的大小")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(ACKS_CONFIG, "acks", "all")
                            .required(false)
                            .description("ack 配置")
                            .build()
            );

        }
    }


}

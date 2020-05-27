package com.starblues.rope.plugins.basic.input;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.support.accept.AbstractAcceptInput;
import com.starblues.rope.core.input.support.accept.BaseAcceptInputConfigParameter;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.ExceptionMsgUtils;
import lombok.Getter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.starblues.rope.utils.ParamUtils.check;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;


/**
 * 接受型 kafka 输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class KafkaInput extends AbstractAcceptInput {

    private final static Logger log = LoggerFactory.getLogger(KafkaInput.class);

    private final static String ID = "kafka_2.12";

    private final Param param;

    private final AtomicBoolean isShutDown = new AtomicBoolean(false);
    private int threads = 1;
    private ExecutorService executorService;
    private List<ConsumerRunner> runnerList = Lists.newArrayList();

    private final ConverterFactory converterFactory;

    public KafkaInput(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
        this.param = new Param();
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "kafka输入";
    }

    @Override
    public String describe() {
        return "kafka 2.12 版本消费者";
    }





    @Override
    public void initialize() throws Exception {
        int threads = param.getThreads();
        if(threads > 0){
            this.threads = threads;
        }
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(processId() + "-kafka-accept-input-%d").build();
        executorService = Executors.newFixedThreadPool(threads, threadFactory);
    }

    @Override
    public BaseAcceptInputConfigParameter configParameter() {
        return param;
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {

        AbstractInputConverter inputConverter =
                converterFactory.getInputConverter(consumer.processId(), String.class);

        for (int i = 0; i < threads; i++) {
            ConsumerRunner consumerRunner = new ConsumerRunner(i, consumer, inputConverter);
            runnerList.add(consumerRunner);
            executorService.submit(consumerRunner);
        }

    }

    @Override
    protected void toStop() throws Exception {
        for (ConsumerRunner consumerRunner : runnerList) {
            try {
                consumerRunner.shutdown();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        executorService.shutdown();
    }


    /**
     * 数据消费线程
     */
    public class ConsumerRunner implements Runnable{

        private final KafkaConsumer<String, String> kafkaConsumer;
        private final Consumer consumer;
        private final AbstractInputConverter inputConverter;

        ConsumerRunner(int num, Consumer consumer,
                       AbstractInputConverter inputConverter){
            Map<String,Object> props = Maps.newHashMap();
            props.putAll(param.getProps());
            props.put("client.id", processId() + "-kafka-accept-input-" + num);
            kafkaConsumer = new KafkaConsumer<String, String>(props);
            this.consumer = consumer;
            this.inputConverter = inputConverter;
        }

        @Override
        public void run() {
            try {
                Duration duration = Duration.of(param.getConsumeTimeout(), ChronoUnit.MILLIS);
                kafkaConsumer.subscribe(param.getTopics());
                while (!isShutDown.get()) {
                    try {
                        accept(duration);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } finally {
                kafkaConsumer.close();
            }
        }

        void accept(Duration duration){
            ConsumerRecords<String, String> records = kafkaConsumer.poll(duration);
            for (ConsumerRecord<String, String> consumerRecord : records) {
                String key = consumerRecord.key();
                String value = consumerRecord.value();
                if(inputConverter != null){
                    // 如果配置了输入转换器, 则使用转换器
                    Record convertRecord = inputConverter.convert(value);
                    consumer.accept(convertRecord);
                } else {
                    // 如果没有使用输入转换器。则使用默认的
                    Record record = new DefaultRecord();
                    Column column;
                    if(StringUtils.isEmpty(key)){
                        column = Column.defaultAuto(value);
                    } else {
                        column = Column.auto(key, value);
                    }
                    record.putColumn(column);
                    consumer.accept(record);
                }
            }
        }


        void shutdown() {
            isShutDown.set(true);
        }

    }


    @Getter
    public static class Param extends BaseAcceptInputConfigParameter{

        public static final String MAP_KEY = "key";
        public static final String MAP_VALUE = "value";

        private static final String TOPICS = "topics";
        private static final String CONSUME_TIMEOUT = "consumeTimeout";

        private static final Integer DEFAULT_CONSUME_TIMEOUT = 1000;
        private static final String THREADS = "threads";

        private static final String CK_SERVERS = "servers";
        private static final String CK_GROUP_ID = "groupId";
        private static final String CK_FETCH_MIN_BYTES = "fetchMinBytes";
        private static final String CK_FETCH_MAX_WAIT_MS = "fetchMaxWaitMs";

        private static final String SSL_ALLOWED = "sslAllowed";
        private static final String CK_SSL_LOCATION = "sslTruststoreLocation";
        private static final String CK_SSL_PASSWORD = "sslTruststorePassword";


        private Set<String> topics;
        private Integer consumeTimeout;
        private int threads = 1;
        private final Map<String, Object> props = Maps.newHashMap();



        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            // set bootstrap.servers
            String servers = check(CK_SERVERS, paramInfo.getString(CK_SERVERS));
            props.put(BOOTSTRAP_SERVERS_CONFIG, servers);

            // 解析topic
            String topics = paramInfo.getString(TOPICS);
            if(StringUtils.isEmpty(topics)){
                throw ExceptionMsgUtils.getInputParamException(ID, TOPICS);
            }


            Iterable<String> split = Splitter.on(",")
                    .omitEmptyStrings()
                    .trimResults()
                    .split(topics);
            this.topics = Sets.newHashSet(split);

            // 消费超时时间
            Integer consumeTimeout = paramInfo.getInt(CONSUME_TIMEOUT);
            if(consumeTimeout == null || consumeTimeout < 0){
                this.consumeTimeout = DEFAULT_CONSUME_TIMEOUT;
            } else {
                this.consumeTimeout = consumeTimeout;
            }

            threads = paramInfo.getInt(THREADS, 1);

            // 配置参数
            props.put(GROUP_ID_CONFIG, check(CK_GROUP_ID, paramInfo.getString(CK_GROUP_ID)));
            props.put(FETCH_MIN_BYTES_CONFIG, paramInfo.getInt(CK_FETCH_MIN_BYTES));
            props.put(FETCH_MAX_WAIT_MS_CONFIG, paramInfo.getInt(CK_FETCH_MAX_WAIT_MS));

            // 默认参数
            props.put(ENABLE_AUTO_COMMIT_CONFIG, true);
            props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
            props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
            props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

            // ssl 配置
            Boolean isSSL = paramInfo.getBoolean(SSL_ALLOWED, false);
            if(isSSL){
                props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,"SSL");
                props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                        check(CK_SSL_LOCATION, paramInfo.getString(CK_SSL_LOCATION)));

                props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                        check(CK_SSL_PASSWORD, paramInfo.getString(CK_SSL_PASSWORD)));
            }
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            configParam.addField(
                    TextField.toBuilder(CK_SERVERS, "代理服务器", "localhost:9092")
                            .required(true)
                            .description("用于建立与Kafka集群的初始连接的列表主机/端口")
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(TOPICS, "topics", "^your-topic$")
                            .required(true)
                            .description("kafka消费的topic, 多个逗号分隔")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(CK_GROUP_ID, "消费组", "")
                            .required(true)
                            .description("消费组id")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(CONSUME_TIMEOUT, "消费间隔时间",
                            DEFAULT_CONSUME_TIMEOUT)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("消费间隔时间, 单位: 毫秒")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(THREADS, "采集的线程数",
                            1)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("采集的线程数量，建议一个Kafka topic，一个线程")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(CK_FETCH_MIN_BYTES, "采集最小字节数",
                            5)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("当一批日志消息的数量达到此字节数，或超过设定的等待时间，即开始采集")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(CK_FETCH_MAX_WAIT_MS, "采集等待时间",
                            100)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("超过此等待时间，或消息累积达到设定的字节数，即开始采集。单位：ms")
                            .build()
            );





            configParam.addField(
                    BooleanField.toBuilder(SSL_ALLOWED, "ssl通信", false)
                            .required(false)
                            .description("是否允许与kafka代理进行ssl通信, " +
                                    "如果启用，与代理的通信将在SSL中完成。请检查kafka代理配置ssl端口")
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(CK_SSL_LOCATION,
                            "信任存储库位置", "")
                            .required(false)
                            .description("信任存储证书文件的路径")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(CK_SSL_PASSWORD,
                            "信任存储密码", "")
                            .required(false)
                            .description("访问信任存储的密码")
                            .build()
            );

        }
    }

}

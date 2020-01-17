# 系统说明

## 核心组件


![image](doc/core.png)


### Input

用于控制数据的输入动作。主要包括两种类型：主动获取型输入、被动接受性输入。

- **主动获取型输入**

1. 介绍
- 主要用于程序获取其他源的数据，比如从数据库、接口等源数据方主动获取数据。
2. 系统实现
- OneReaderInput：只执行一次的输入
- PeriodAcquireReaderInput：周期性执行的输入
- QuartzReaderInput: 基于Quartz的定时输入
- 可自行扩展

- **被动接受型输入**
1. 介绍
- 主要用于程序被动接受其他源发送过来的数据。比如从kafka消费数据。
2. 系统实现
- 该输入需要自主扩展实现。只需要继承 `AbstractAcceptInput` 抽象类， 实现其抽象方法即可。详细参考下文说明。

### Reader
1. 介绍
- 该组件主要是读取数据的具体实现，可自行扩展。不能单独运行，需要作用于 `主动获取型Input`上才能运行。
2. 实现
- 读取数据组件只需实现 `Reader` 接口即可。详细参考下文说明。
### Transport
1. 介绍
- 该组件主要用于数据传输，其作用是中间桥接数据管道。
2. 传输器分类
- 输入传输器：用于将数据从`Input`组件传输到`Transform`组件
- 输出传输器：用于将数据从`Transform`组件传输到`Output`组件
3. 传输器实现
- DefaultInputTransport：默认的输入传输器
- DefaultOutputTransport：默认的输出传输器
- BufferTransport：缓冲传输器（输入、输出皆可用）
- 用户也可自行扩展任意的数据传输器。
### DateHandler
1. 介绍
- 该组件为数据处理器，其作用是处理数据，比如将字符型数据处理成其他类型、丢弃某数据、新增某数据字段。该组件可自行扩展，是非必须组件。
2. 系统实现
- 实现 `DateHandler` 接口即可。详细参考下文说明。
### Output
1. 介绍
- 该组件主要用于系统数据的输出，即将数据以哪种方式输出，它并不关心数据输出到哪里去，只关心数据如何输出。
2. 系统实现
- SimpleOutput：简单的输出
- BatchOutput：批量输出
- TimeIntervalOutput：以设定时间间隔输出
- DataSizeOutput：数据字节量输出【暂未实现】
- TimeIntervalBatchOutput：时间间隔、批量组合输出
- TimeIntervalDataSizeOutput：时间间隔、数据字节量批量组合输出【暂未实现】
- 可自行扩展
### Writer
1. 介绍
- 该组件为具体数据写入的实现，它主要关心数据写入到哪里去，为数据输出的具体实现。必须作用于`Output`上才能运行。
2. 系统实现
- 定制化型数据写入者：该写入者一般用于用户定制化写入，需要继承 `AbstractWriter` 抽象类，实现其抽象方法即可。详细参考下文说明。
- 数据转换器型数据写入者：该写入者一般配合数据转换器`Converter`实现，因此就具备了扩展性，该写入者的功能主要关心数据写入的实现细节，具体如何转换数据类型可由数据转换器`Converter`实现完成。该写入者需要继承`AbstractConverterWriter` 抽象类，实现其抽象方法即可。详细参考下文说明。
### Converter
1. 介绍
- 数据转换器，该组件主要用于数据类型的转换，大部分情况是配合`Reader` `Writer`实现的，核心思想是让`Reader` `Writer`关心数据的读取、写入，它关心数据类型的转换细节。这样就具有很强的扩展性。
- 分类
- 输入转换器：主要用于输入数据的格式转换。继承 `AbstractInputConverter` 抽象类，实现其抽象方法即可。详细参考下文说明。
- 写入者转换器：主要用于数据写入的格式转换。继承 `AbstractWriterConverter` 抽象类，实现其抽象方法即可。详细参考下文说明。
- 可自行扩展


## 配置说明

### 系统配置
1. 插件配置
``` yml
plugin:
  runMode: dev
  pluginPath: ./plugins
  pluginConfigFilePath:
  backupPluginPath: backupPlugin
  uploadTempPath: temp
```
- runMode：运行项目时的模式。分为开发环境(dev)、生产环境(prod)
- pluginPath: 插件的路径。开发环境建议直接配置为插件模块的父级目录。例如: plugins。如果启动主程序时, 插件为加载, 请检查该配置是否正确。
- pluginConfigFilePath: 在生产环境下, 插件的配置文件路径。在生产环境下， 请将所有插件使用到的配置文件统一放到该路径下管理。如果启动主程序时, 报插件的配置文件加载错误, 有可能是该该配置不合适导致的。
- backupPluginPath：备份插件包的目录
- uploadTempPath：上传的插件所存储的临时目录


### data-transfer 配置
``` yml
data-transfer:
  # 名称
  name: 开发环境测试
  # 输入管理器的配置
  inputManager:
    # 周期性获取数据的配置。基于 java 的调度器实现
    periodAcquire:
      # 核心线程池的数量。>=0
      corePoolSize: 0
  # 输出管理器的配置
  outputManager:
    # 周期性输出的配置
    timeInterval:
      # 核心线程池的数量。>=0
      corePoolSize: 0
  # 输出线程池的配置
  outputPoll:
    # 核心线程数. 默认 0
    corePoolSize: 0
    # 最大线程数.默认: 2147483647
    maximumPoolSize: 2147483647
    # 线程存活时间。单位：毫秒.默认0
    keepAliveTimeMs: 0
  # 输入数据传输器的配置
  inputTransport:
    # 传输器id。可选：default、buffer
    id: buffer
    # 转换器的参数。当前配置参数为 buffer 需要的参数
    params:
      # 缓冲大小。必须为 2 的n次方
      bufferSize: 65536
      # 缓冲消费者等待策略, 可选: sleeping, yielding, blocking, busy_spinning
      waitStrategy: blocking
      # 缓冲消费池的消费者数量
      consumePoolSize: 1
  # 输出数据传输器的配置。同 inputTransport 配置
  outputTransport:
    id: buffer
    params:
      bufferSize: 65536
      waitStrategy: blocking
      consumePoolSize: 1
  # 流程存储的配置
  processStorages:
    # 内存流程存储者
      # 存储者id
    - id: memory
    # json 文件流程存储者
      # 存储者id
    - id: json-file
      # 存储者所需配置
      params:
        # 文件存储目录
        storePath: D:\data-transfer\json
    # yml 文件流程存储者
      # 存储者id
    - id: yml-file
      # 存储者所需配置
      params:
        # 文件存储目录
        storePath: D:\data-transfer\yml

```
1. inputManager 输入管理器
-  periodAcquire - 周期性输入配置
```yml
periodAcquire:
    corePoolSize：运行周期性输入的核心线程数。必须大于等于 0
```
- quartz - quartz框架的配置
```yml
quartz：
  key: value 
详细项可百度搜索quartz的配置
```
2. outputManager 输出管理器配置
- timeInterval 周期性时间输出的配置
```yml
corePoolSize：运行周期性输入的核心线程数。必须大于等于 0
```

3. outputPoll 输入线程池的配置
- corePoolSize: 核心线程数，默认0。范围： 0<=n<=2147483647
- maximumPoolSize: 最大线程数，默认2147483647。范围： 0<n<=2147483647
- keepAliveTimeMs: 线程存活时间，默认0。范围：n>=0

3. inputTransport、outputTransport 配置

- default - 默认的Transport配置
```yml
id: default
params: 
```
- buffer - 缓冲的的Transport配置
```yml
id: buffer
params: 
  # 缓冲大小。必须为 2 的n次方
  bufferSize: 65536
  # 缓冲消费者等待策略, 可选: sleeping, yielding, blocking, busy_spinning
  waitStrategy: blocking
  # 缓冲消费池的消费者数量
  consumePoolSize: 1
```
4. processStorages
- 流程存储器的配置
- 目前系统支持如下流程存储器
```text
memory: 内存存储流程, 它不会持久化流程。
json-file：json格式的流程存储器，它会将流程存储为json格式的文件。所需配置参数：storePath 文件存储目录
yml-file：yml格式的流程存储器，它会将流程存储为yml格式的文件。所需配置参数：storePath 文件存储目录

```

## 流程管理器的配置
1. 核心配置

配置格式如下：
```yml
processId: 流程id, 全局唯一, 不能重复
name：流程名称
followStart：是否跟随系统启动而启动该流程。true 跟随系统启动，false 不跟随系统启动
input：输入的配置
dateHandlerFlow：数据转换器的配置
output：输出的配置
```

2. input 输入的配置说明。

配置格式如下：

```yml
input：
  id: xx
  params:
    param1: a
    param2: b
  reader:
    id: yy
    params:
      param1: a
      param2: b
```
 
- 执行一次的输入的配置
```yml
id: one
```

- 周期性的输入的配置
```yml
id: period-acquire
params:
  period: 周期数
  delay： 延迟执行数
  timeUnit：时间单位。可选：NANOSECONDS、MICROSECONDS、MILLISECONDS、SECONDS、MINUTES、HOURS、DAYS
```

- quartz 输入的配置
```yml
id: quartz
params:
  triggerCron：quartz表达式
  delaySeconds：延迟执行。单位秒
  jobName: 任务名称。全局不可重复。可选
  triggerName：调度器的名称。全局不可重复。可选
```

- 数据读取器的配置。配置格式如下
```yml
reader:
  # 为数据读取者的id
  id: yy
  # 数据读取者所需参数。可选
  params:
    param1: a
    param2: b
```

**数据读取者可自行扩展。配置详细参考具体数据读取者。**

3. dateHandlerFlow：数据转换器的配置说明

可配置多个转换器，执行顺序为所配置的顺序。

配置格式如下：
```yml
dateHandlerFlow:
  - id: set-time
    params:
      key: process1-time
      format: yyyy-MM-dd HH:mm:ss
  - id: xxx
    params:
      param1: a
      param2: b
```
**数据转换器可自行扩展。详细配置参考具体数据转换器。**

4. output 输出的配置

配置格式如下：
```yml
output:
  id: batch
  params:
    batchSize: 5
  writers:
    - id: simple-file
      params:
        filePath: D:\aa\zhuozhuo.txt
```

- 简单的输出配置

说明：数据进入就会被输出。没有输出策略

```
id: simple
```

- 批量输出的配置


说明：当达到指定数据量时进行输出

```
id: batch
params:
  batchSize: 批量数据大小
```

- 周期性输出的配置

说明：每隔设定时间间隔进行数据输出

```
id: time-interval
params:
  timeInterval: 时间间隔数。单位秒
```

- 其他输入配置待续


- writers 配置。主要配置该输出对应的写入者。可配置多个

配置格式如下：

```yml
writers:
  # 为数据写入者的id
  - id: xx1
    # 自定义写入者code. 同一个输出的写入者code不能相同
    code: code1
    # 该输入写入者的参数。可选
    params:
      param1: xx
    # 如果该数据写入者需要数据转换器，则可配置。可选
    converter:
      id: xx
      params:
        param1: xx
  - id: xx2
    # 自定义写入者code. 同一个输出的写入者code不能相同
    code: code2
    params:
      param1: xx
    converter:
      id: xx
      params:
        param1: xx
```

**数据写入者可自行扩展。详细配置参考具体数据写入者。**

## 扩展文档

**下面的扩展都可在插件中实现。**

### 公共说明
- 下述都存在实现 标识`Identity`接口。该接口用于标识一个身份，主要需要实现如下方法：

``` java
/**
 * 全局唯一id
 * @return String
 */
String id();

/**
 * 名称
 * @return String
 */
String name();

/**
 * 描述
 * @return String
 */
String describe();

```

- 参数说明：在某些时候下面的实现需要自定义配置参数，则可以根据自定义的参数Bean，将其以泛型定义，在初始化阶段，系统会自动将其定义的参数传入。

### 数据读取者
1. 实现接口 `Reader`
2. 生命周期
- initialize[初始化] -> reader[读取数据] -> destroy[销毁]。
- 初始化和销毁只执行一次，读取数据的实现是由`Input`控制调用。
3. 具体实现-随机输入
``` java
import com.google.common.collect.Lists;
import Reader;
import Column;
import DefaultRecord;
import Record;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 随机输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class RandomReader implements Reader<RandomReader.Param> {

    private final static String ID = "random";

    private Random rand = new Random();
    private RandomReader.Param param;


    @Override
    public void initialize(String processId, RandomReader.Param param) throws Exception {
        this.param = param;
    }

    @Override
    public Class<RandomReader.Param> paramClass() {
        return RandomReader.Param.class;
    }

    @Override
    public List<Record> reader() throws Exception {
        Record record = new DefaultRecord();
        Integer i = rand.nextInt(param.getMaxNum());
        record.putColumn(Column.auto(param.getKey(), i));
        return Lists.newArrayList(record);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return "随机输入";
    }

    @Data
    public static class Param{
        private String key;
        private Integer maxNum;
    }

}


```

### 被动接受型输入-AbstractAcceptInput
1. 继承抽象类 `AbstractAcceptInput`
2. 生命周期
- initialize[初始化] -> toStart[启动] -> toStop[停止]。
- 每个阶段只执行一次。具体如何接受数据可在启动中实现。
3. 具体实现-kafka 数据消费数据输入
```java

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import AbstractInputConverter;
import ConverterFactory;
import AbstractAcceptInput;
import Column;
import DefaultRecord;
import Record;
import com.sino.datatransfer.core.parameter.Parameter;
import Converter;
import ExceptionMsgUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kafka 2.12 版本
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class KafkaInput extends AbstractAcceptInput<KafkaInput.Param> {

    private final static String ID = "kafka_2.12";

    private Param param;

    private final AtomicBoolean isShutDown = new AtomicBoolean(false);
    private ExecutorService singleThreadPool;
    private KafkaConsumer<String, String> consumer;

    private final ConverterFactory converterFactory;

    public KafkaInput(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return "kafka 2.12 版本消费者";
    }


    @Override
    public void initialize(Param param) throws Exception {
       this.param = Objects.requireNonNull(param);

    }

    @Override
    protected void toStart(AcceptTransport acceptTransport) throws Exception {
        consumer = new KafkaConsumer<String, String>(param.getProperties());
        consumer.subscribe(param.getTopics());

        AbstractInputConverter inputConverter =
                converterFactory.getInputConverter(acceptTransport.getProcessId(), String.class);


        String poolName = ID + "[" +acceptTransport.getProcessId() +"]-%d";
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(poolName).build();

        singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        Duration duration = Duration.of(param.getConsumeTimeout(), ChronoUnit.MILLIS);
        singleThreadPool.execute(() -> {
            while (!isShutDown.get()) {
                ConsumerRecords<String, String> records = consumer.poll(duration);
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    if(inputConverter != null){
                        // 如果配置了输入转换器, 则使用转换器
                        Record convertRecord = inputConverter.convert(consumerRecord.value());
                        acceptTransport.input(convertRecord);
                    } else {
                        // 如果没有使用输入转换器。则使用默认的
                        Record record = new DefaultRecord();
                        record.putColumn(Column.auto(consumerRecord.key(), consumerRecord.value()));
                        acceptTransport.input(record);
                    }
                }
            }
            consumer.close();
        });
    }


    @Override
    protected void toStop() throws Exception {
        isShutDown.set(true);
        singleThreadPool.shutdown();
    }


    @Override
    public Param getParam() {
        return param;
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class Param implements Parameter{

        public static final String TOPICS = "topics";
        public static final String CONSUME_TIMEOUT = "consumeTimeout";


        private Set<String> topics;
        private Long consumeTimeout;
        private Properties properties;

        @Override
        public Parameter parsing(Properties properties) throws Exception {
            String topics = Converter.getAsString(properties.get(TOPICS));
            if(StringUtils.isEmpty(topics)){
                throw ExceptionMsgUtils.getInputParamException(ID, TOPICS);
            }
            Iterable<String> split = Splitter.on(",")
                    .omitEmptyStrings()
                    .trimResults()
                    .split(topics);
            this.topics = Sets.newHashSet(split);
            properties.remove(TOPICS);

            Long consumeTimeout = Converter.getAsLong(properties.get(CONSUME_TIMEOUT));
            if(consumeTimeout == null || consumeTimeout < 0L){
                this.consumeTimeout = 1000L;
            } else {
                this.consumeTimeout = consumeTimeout;
            }
            properties.remove(CONSUME_TIMEOUT);

            this.properties = properties;
            return this;
        }
    }


}


```

### 数据处理器
1. 实现接口 `DateHandler`
2. 生命周期
- initialize[初始化] -> handle[处理数据] -> destroy[销毁]。
- initialize和destroy阶段只执行一次。handle 每进入一条数据进行一次调用。
3.具体实现-设置时间
```java
import DateHandler;
import Column;
import Record;
import TimeUtil;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 设置日期数据处理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SetTimeDateHandler implements DateHandler<SetTimeDateHandler.Param> {

    public static final String ID = "set-time";

    private SetTimeDateHandler.Param param;

    @Override
    public void initialize(String processId, SetTimeDateHandler.Param param) throws Exception {
        this.param = param;
    }

    @Override
    public Class<SetTimeDateHandler.Param> paramClass() {
        return SetTimeDateHandler.Param.class;
    }

    @Override
    public Record handle(Record record) throws Exception{
        String format = param.getFormat();
        String time = null;
        if(StringUtils.isEmpty(format)){
            time = TimeUtil.getNowTimeToMs();
        } else {
            time = TimeUtil.getNowTimeByFormat(format);
        }
        record.putColumn(Column.auto(param.getKey(), time));
        return record;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return "设置指定格式的日子";
    }

    @Data
    public static class Param{
        private String key;
        private String format;
    }

}

```


### 数据写入者
1. 定制化型写入者：继承抽象类 `AbstractWriter`。
2. 数据转换器型化写入者：继承抽象类 `AbstractConverterWriter`
- 它的泛型参数：第一个为转换器转换出的结果类型，第二个为该写入者的参数类型[无传入参数的话，可写Object类型]
3. 生命周期
- initialize[初始化] -> convert[转换数据类型]
- initialize阶段只执行一次。每写入一条数据则执行一次convert
4. 具体实现-json 测试转换器
```java
import com.google.gson.Gson;
import AbstractInputConverter;
import Column;
import DefaultRecord;
import Record;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * json 测试转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class TestConverter extends AbstractInputConverter<String, String> {

    public static final String ID = "test";

    private String string;

    private final Gson gson;

    public TestConverter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void initialize(String processId, String s) throws Exception {
        this.string = s;
    }

    @Override
    public Record convert(String s) {
        Map map = gson.fromJson(s, Map.class);
        Record record = new DefaultRecord();
        map.forEach((k,v)->{
            record.putColumn(Column.auto(String.valueOf(k), v));
        });
        record.putColumn(Column.auto("s", string));
        return record;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return ID;
    }

}

```
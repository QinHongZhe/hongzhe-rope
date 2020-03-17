# 介绍
Rope 是一款轻量级别的ETL(Extract-Transform-Load)工具。主要用于从不同源获取/接受数据，然后统一处理数据后，写入到各种目标源；系统采用多级缓冲和数据缓存，每秒可处理上万级别的数据；而且系统采用插件扩展系统的各个组件，针对不同需求扩展不同插件。
# 特性
1. 轻量级别、快速、简单，入门门槛低
2. 基于`Springboot`开发
3. 扩展性强，基于插件开发，可根据不同需求来开发数据读取者、数据处理器、数据写入者
4. 既可通过UI界面来构建流程、也可以使用`json`、`yml`文件构建流程
5. 基于 `Disruptor` 做的缓冲，并新增缓存(内存、redis、rocksdb等)，处理速度快
# 架构图
![架构图](https://images.gitee.com/uploads/images/2020/0117/153409_1eb1d6b8_5053202.png "core.png")
# 核心模块
## 输入模块
用于控制数据的输入动作。主要包括两种类型：主动获取型输入、被动接受性输入。

## 数据读取者
该模块主要是读取数据的具体实现，可自行扩展。不能单独运行，需要作用于 `主动获取型Input`上才能运行。

## 数据传输模块
主要对数据进行传送，系统中存在两种数据传输：一个是用于把输入模块输入的数据传输到数据处理器中，另一个是把数据处理器处理后的数据传输到输出模块中。目前支持默认的数据传输器和基于`Disruptor`的缓冲数据传输器，在实际环境中建议`Disruptor`的缓冲数据传输器。

## 数据处理器模块
该组件为数据处理器，其作用是处理数据，比如将字符型数据处理成其他类型、丢弃某数据、新增某数据字段。该组件可自行扩展，是非必须组件。

## 数据输出模块
该组件主要用于系统数据的输出，即将数据以哪种方式输出，它并不关心数据输出到哪里去，只关心数据如何输出。

## 数据写入者
该组件为具体数据写入的实现，它主要关心数据写入到哪里去，为数据输出的具体实现。必须作用于`Output`上才能运行

## 数据转换器-Converter
数据转换器，该组件主要用于数据类型的转换，大部分情况是配合`Reader` `Writer`实现的，核心思想是让`Reader` `Writer`关心数据的读取、写入，它关心数据类型的转换细节。这样就具有很强的扩展性。


## 详细文档
[点击查看文档](https://gitee.com/starblues/rope/wikis/pages)

## 开发环境配置
详见文档：[开发环境运行与配置文档](https://gitee.com/starblues/rope/wikis/pages?sort_id=2005921&doc_id=507971)

## 发行版下载

- V1.0.0：[https://gitee.com/starblues/rope/attach_files/349986/download](https://gitee.com/starblues/rope/attach_files/349986/download)

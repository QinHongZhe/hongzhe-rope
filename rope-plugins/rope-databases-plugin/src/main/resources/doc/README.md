# 插件

## 插件描述
```text
数据库读写插件
```

## 插件配置

```text
databases:
  - key: mysql-config1
    name: 名称
    jarPath: D:\code-workspace\java\aiops-git\aiops-collector\aiops-data-transfer\plugins\databases-plugin\jdbc-jar\mysql.jar
    config:
      url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8
      username: root
      password: 123456
      driverClassName: com.mysql.jdbc.Driver
      initialSize：0
      maxActive：8
      minIdle：0
      maxWait：-1
```


配置说明:
- key: 当前数据库配置的唯一key。自定义, 但不能重复
- name: 当前数据库配置的名称，自定义。
- jarPath：当前数据库驱动jar包路径，绝对路径
- config：druid数据库连接池配置。详细配置参考: [https://www.cnblogs.com/KohnKong/articles/6014918.html](https://www.cnblogs.com/KohnKong/articles/6014918.html)


# Reader

## 数据库简单读取者

### Key

**database-simple-reader**

### 描述

用于从数据库中简单读取数据

### 参数

```text
databaseKey: 数据库定义的key
querySql: 查询sql
```

参数说明：

- databaseKey：选用哪一个数据库定义的key。详见：配置文件中 databases->key。
- querySql：自定义查询数据的sql


### 配置样例

```text
id: simple-mysql-reader
params:
  - databaseKey: mysql-config1
    querySql: select `id`,`age`,`data`,`time`,`int1`,`int2` from `student`
```
## 数据库通用读取者

### Key

**database-common-reader**

### 描述

用于从数据库中通用读取数据，支持分页读取和增量读取。

### 参数

```text
databaseKey: 数据库定义的key
readerKey: 读写者的KEY
dataSync: 数据库同步方式
pagination: 分页方式
pageSize: 分页大小
sqlIncrementValueFields: 增量字段和初始值
querySql: 查询sql
```

参数说明：

- databaseKey：选用哪一个数据库定义的key。详见：配置文件中 databases->key。
- readerKey：用于区分多个Process
- dataSync：分为全量(full_replace)和增量(increment)同步方式
- pagination：分页的方式，分为startRow-EndRow、startRow-pageSize、none三种方式
- pageSize：分页的大小
- sqlIncrementValueFields：增量字段和初始值，格式为field1=initValue1。field1为字段名，initValue1为初始值
- querySql：自定义查询数据的sql


### 配置样例

```text
    "id": "database-common-reader",
    "params": {
        "databaseKey": "mysql-config1",
        "readerKey": "uniquekey1",
        "dataSync": "full_replace",
        "pagination": "startRow-pageSize",
        "sqlIncrementValueFields": "",
        "pageSize": 10,
        "querySql": "select id,name from res where 1=1 order by CAST(`id` AS DECIMAL) asc "
    }
```

# Writer

## 数据库简单写入者

### Key

**database-simple-writer**

### 描述

简单的将数据写入到数据库中


### 参数

```text
databaseKey: 数据库定义的key
tableName: 表名称
id：表主键字段
idType：主键字段类型
createTableSql：创建表的sql. 如果表不存在，则使用该sql创建表
clean：写入数据前是否删除表数据
cleanSql：删除表数据的sql
fieldMappings: 字段映射key-value配置
```

参数说明：

- databaseKey：选用哪一个数据库定义的key。详见：配置文件中 databases->key
- tableName：表名称。要操作表的表名
- id：表主键字段
- idType：主键字段类型。可选：uuid(uuid类型)、auto(自增类型)、follow(将定义的写入字段值填充为主键值)
- clean：写入数据前是否删除表数据。true 清除，false 不清除
- cleanSql：删除表数据的sql。clean为true有效
- fieldMappings：字段映射key-value配置。key为写入记录的key, value为表中字段的key


### 配置样例

```json
"id": "simple-mysql-writer",
"params": {
  "databaseKey": "mysql-config1",
  "tableName": "zhuo",
  "id": "id",
  "idType": "uuid",
  "createTableSql": "CREATE TABLE `zhuo` (`id` varchar(32) CHARACTER SET latin1 NOT NULL, `age` int(11) DEFAULT NULL, `data` varchar(32) CHARACTER SET latin1 DEFAULT NULL, `time` varchar(32) DEFAULT NULL, `myint` int(10) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8",
  "clean": true,
  "cleanSql": "delete * from zhuo",
  "fieldMappings": {
    "id": "id",
    "age": "age",
    "data": "data",
    "int1": "myint"
  }
}
```
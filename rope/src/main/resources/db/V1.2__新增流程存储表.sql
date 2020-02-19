-- ----------------------------
-- Table structure for process
-- ----------------------------
drop table if exists process;
create table process (
  process_id varchar(32) NOT NULL primary KEY COMMENT '流程主键',
  name varchar(32) NOT NULL COMMENT '名称',
  describe varchar(128) DEFAULT NULL COMMENT '描述',
  is_start boolean DEFAULT false COMMENT '是否启动',
  process_input_id varchar(32) NOT NULL COMMENT '关联输入主键',
  process_date_handler_flow_id varchar(32) DEFAULT NULL COMMENT '关联流程数据处理者主键',
  process_output_id varchar(32) NOT NULL COMMENT '关联输出主键'
);


-- ----------------------------
-- Table structure for process_date_handler_flow
-- ----------------------------
drop table if exists process_date_handler_flow;
create table process_date_handler_flow (
  process_date_handler_flow_id varchar(32) NOT NULL primary KEY COMMENT '流程数据处理者主键',
  process_id varchar(32) NOT NULL COMMENT '关联流程主键',
  process_date_handler_id varchar(32) NOT NULL COMMENT '关联数据处理者主键',
  date_handler_order int DEFAULT 0 COMMENT '顺序, 数字越小越靠前'
);


-- ----------------------------
-- Table structure for process_common_config
-- ----------------------------
drop table if exists process_common_config;
create table process_common_config (
  common_config_id varchar(32) NOT NULL primary KEY COMMENT '公用配置主键',
  name varchar(32) NOT NULL COMMENT '名称',
  describe varchar(128) DEFAULT NULL COMMENT '描述',
  id varchar(64) NOT NULL COMMENT '公用配置的id',
  params longvarchar DEFAULT NULL COMMENT '参数json'
);

-- ----------------------------
-- Table structure for process_input
-- ----------------------------
drop table if exists process_input;
create table process_input (
  process_input_id varchar(32) NOT NULL primary KEY COMMENT '流程输入主键',
  common_config_id varchar(32) NOT NULL COMMENT '输入的公用配置-关联公用配置主键',
  process_reader_id varchar(32) NOT NULL COMMENT '关联数据读取者主键',
  process_converter_id varchar(32) DEFAULT NULL COMMENT '关联数据转换器主键'
);


-- ----------------------------
-- Table structure for process_reader
-- ----------------------------
drop table if exists process_reader;
create table process_reader (
  process_reader_id varchar(32) NOT NULL primary KEY COMMENT '数据读取者主键',
  common_config_id varchar(32) NOT NULL COMMENT '数据读取者的公用配置-关联公用配置主键'
);


-- ----------------------------
-- Table structure for process_converter
-- ----------------------------
drop table if exists process_converter;
create table process_converter (
  process_converter_id varchar(32) NOT NULL primary KEY COMMENT '数据转换器主键',
  common_config_id varchar(32) NOT NULL COMMENT '数据转换器的公用配置-关联公用配置主键'
);

-- ----------------------------
-- Table structure for process_date_handler
-- ----------------------------
drop table if exists process_date_handler;
create table process_date_handler (
  process_date_handler_id varchar(32) NOT NULL primary KEY COMMENT '数据处理者主键',
  common_config_id varchar(32) NOT NULL COMMENT '数据转换器的公用配置-关联公用配置主键'
);


-- ----------------------------
-- Table structure for process_output
-- ----------------------------
drop table if exists process_output;
create table process_output (
  process_output_id varchar(32) NOT NULL primary KEY COMMENT '流程输出主键',
  common_config_id varchar(32) NOT NULL COMMENT '输出的公用配置-关联公用配置主键'
);


-- ----------------------------
-- Table structure for process_output_writer
-- ----------------------------
drop table if exists process_output_writer;
create table process_output_writer (
  process_output_writer_id varchar(32) NOT NULL primary KEY COMMENT '流程输出写入者关联表主键',
  process_output_id varchar(32) NOT NULL COMMENT '流程输出主键',
  process_writer_id varchar(32) NOT NULL COMMENT '流程写入者主键'
);

-- ----------------------------
-- Table structure for process_writer
-- ----------------------------
drop table if exists process_writer;
create table process_writer (
  process_writer_id varchar(32) NOT NULL primary KEY COMMENT '流程写入者主键',
  common_config_id varchar(32) NOT NULL COMMENT '写入者的公用配置-关联公用配置主键',
  code varchar(32) NOT NULL COMMENT '写入者的code-写入者唯一的code',
  process_converter_id varchar(32) DEFAULT NULL COMMENT '关联数据转换器主键'
);



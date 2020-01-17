drop table if exists process_info;
create table process_info(
    process_id varchar(32) primary key comment '主键',
    process_json json not null comment '流程json'
);


drop table if exists sync_info;
create table sync_info(
    id varchar(32) primary key comment '同步者的id',
    name varchar(32) not null comment '同步者的名称',
    value varchar(128) not null comment '同步者的值'
);
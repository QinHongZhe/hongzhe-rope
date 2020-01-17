-- ----------------------------
-- Table structure for user
-- ----------------------------
drop table if exists user;
create table user (
  user_id varchar(32) NOT NULL primary KEY COMMENT '用户id',
  name varchar(32) DEFAULT NULL COMMENT '名称',
  username varchar(32) NOT NULL COMMENT '用户名',
  password varchar(255) NOT NULL COMMENT '密码',
  salt varchar(64) NOT NULL DEFAULT '' COMMENT '加盐的字符串',
  hash_iterations int(1) NOT NULL DEFAULT '2' COMMENT 'hash迭代次数',
  ttl_millis int(11) DEFAULT '3600000' COMMENT '过期时间戳'
);

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO user VALUES ('1', '超级管理员', 'admin', '043f670a03227b0206cd6cc8847df911', 'fb84953ececb4f68bd6fe38b2bcf357a', '2', '3600000');
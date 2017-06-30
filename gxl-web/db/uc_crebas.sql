/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/6/28 13:01:51                           */
/*==============================================================*/


drop table if exists UC_USER_CREDENCE_INFO;

drop table if exists UC_USER_INFO;

/*==============================================================*/
/* Table: UC_USER_CREDENCE_INFO                                 */
/*==============================================================*/
create table UC_USER_CREDENCE_INFO
(
   CREDENCE_ID          char(32) comment '凭证ID',
   USER_ID              char(32) comment '用户ID',
   CREDENCE_TYPE        char(8) comment '凭证类型',
   SALT                 char(8) comment '散列值',
   PASSWORD             char(40) comment '密码',
   VALID_FLAG           char(8) comment '有效标志',
   EFFECT_DATE          date comment '生效时间',
   INVALID_DATE         date comment '失效时间'
);

alter table UC_USER_CREDENCE_INFO comment '用户凭证信息表';

/*==============================================================*/
/* Table: UC_USER_INFO                                          */
/*==============================================================*/
create table UC_USER_INFO
(
   USER_ID              char(32) comment '用户ID',
   USER_CODE            char(20) comment '用户编号',
   USER_NAME            char(40) comment '用户名称',
   MOBILE_NUM           char(11) comment '手机号码',
   EMAIL                char(100) comment '电子邮箱',
   STATUS               char(8) comment '状态',
   VALID_FLAG           char(8) comment '有效标志'
);

alter table UC_USER_INFO comment '用户信息表';


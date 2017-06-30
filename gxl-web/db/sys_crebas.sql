/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/6/28 12:30:52                           */
/*==============================================================*/


drop table if exists SYS_CODE_INFO;

drop table if exists SYS_MENU_INFO;

/*==============================================================*/
/* Table: SYS_CODE_INFO                                         */
/*==============================================================*/
create table SYS_CODE_INFO
(
   CODE_ID              char(36),
   CODE_TYPE_CODE       char(40),
   CODE_TYPE_NAME       char(40),
   CODE_CODE            char(40),
   CODE_NAME            char(40),
   CODE_VALUE           char(8),
   VALID_FALG           char(8),
   REMARK               varchar(256)
);

alter table SYS_CODE_INFO comment '系统代码表';

/*==============================================================*/
/* Table: SYS_MENU_INFO                                         */
/*==============================================================*/
create table SYS_MENU_INFO
(
   MENU_NAME            char(40),
   MENU_TYPE            char(8),
   MENU_ID              char(32),
   P_MENU_ID            char(32),
   DISPLAY_FLAG         char(8),
   VALID_FLAG           char(8),
   REMARK               varchar(256)
);

alter table SYS_MENU_INFO comment '系统菜单表';


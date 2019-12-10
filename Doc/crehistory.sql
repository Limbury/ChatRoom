/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/12/10 10:49:50                          */
/*==============================================================*/


drop table if exists history;

/*==============================================================*/
/* Table: history                                               */
/*==============================================================*/
create table history
(
   uid1                 int not null,
   uid2                 int not null,
   date                 datetime not null default CURRENT_TIMESTAMP,
   text                 text,
   primary key (uid1, uid2, date)
);

alter table history comment '聊天记录';


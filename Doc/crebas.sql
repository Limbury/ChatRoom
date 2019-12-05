/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/12/4 20:30:36                           */
/*==============================================================*/


drop table if exists relationship_1;

drop table if exists uid_lid;

drop table if exists userinfo;

/*==============================================================*/
/* Table: relationship_1                                        */
/*==============================================================*/
create table relationship_1
(
   lid                  int not null,
   uid                  int not null,
   primary key (lid, uid)
);

alter table relationship_1 comment '一个用户又多个好友分组';

/*==============================================================*/
/* Table: uid_lid                                               */
/*==============================================================*/
create table uid_lid
(
   lid                  int not null,
   uid                  int,
   lname                text not null,
   primary key (lid)
);

alter table uid_lid comment '好友分组';

/*==============================================================*/
/* Table: userinfo                                              */
/*==============================================================*/
create table userinfo
(
   uid                  int not null,
   pwd                  text not null,
   nickname             text not null,
   avatar               int not null,
   primary key (uid)
);

alter table userinfo comment '用户信息';

alter table relationship_1 add constraint FK_relationship_1 foreign key (uid)
      references userinfo (uid) on delete restrict on update restrict;

alter table relationship_1 add constraint FK_relationship_2 foreign key (lid)
      references uid_lid (lid) on delete restrict on update restrict;

alter table uid_lid add constraint FK_relationship_3 foreign key (uid)
      references userinfo (uid) on delete restrict on update restrict;


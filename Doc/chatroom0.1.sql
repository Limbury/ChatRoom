/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/11/26 22:00:28                          */
/*==============================================================*/


drop table if exists device;

drop table if exists link_man;

drop table if exists man_list;

drop table if exists message;

/*==============================================================*/
/* Table: device                                                */
/*==============================================================*/
create table device
(
   device_id            blob not null,
   man_id               char(10),
   primary key (device_id)
);

/*==============================================================*/
/* Table: link_man                                              */
/*==============================================================*/
create table link_man
(
   man_id               char(10) not null,
   nickname             char(20),
   passwd               char(10),
   primary key (man_id)
);

/*==============================================================*/
/* Table: man_list                                              */
/*==============================================================*/
create table man_list
(
   man_id               char(10) not null,
   list                 char(2000),
   primary key (man_id)
);

/*==============================================================*/
/* Table: message                                               */
/*==============================================================*/
create table message
(
   dest_id              char(10) not null,
   man_id               char(10) not null,
   record               varchar(1024),
   time                 time,
   primary key (dest_id, man_id)
);

alter table device add constraint FK_Reference_1 foreign key (man_id)
      references link_man (man_id) on delete restrict on update restrict;

alter table man_list add constraint FK_Reference_2 foreign key (man_id)
      references link_man (man_id) on delete restrict on update restrict;

alter table message add constraint FK_Reference_3 foreign key (man_id)
      references link_man (man_id) on delete restrict on update restrict;


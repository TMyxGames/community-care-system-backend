create table carousel
(
    id         int auto_increment
        primary key,
    sort_order int default 0 null,
    title      varchar(255)  null,
    img_url    varchar(255)  null,
    link       varchar(255)  null
);

create table evaluation
(
    id           int auto_increment
        primary key,
    user_id      int      null,
    order_id     int      null,
    service_id   int      null,
    staff_id     int      null,
    content      text     null,
    service_rate int      null,
    staff_rate   int      null,
    create_time  datetime null
);

create table location
(
    user_id     int            not null
        primary key,
    lng         decimal(10, 6) null,
    lat         decimal(10, 6) null,
    update_time datetime       null
);

create table message
(
    id              int auto_increment
        primary key,
    from_session_id int      null,
    to_session_id   int      null,
    from_id         int      null,
    to_id           int      null,
    content         text     null,
    type            int      null,
    status          int      null,
    send_time       datetime null
);

create table service
(
    id          int auto_increment
        primary key,
    sort_order  int            null,
    title       varchar(255)   null,
    type        varchar(50)    null,
    introduce   varchar(255)   null,
    content_url varchar(255)   null,
    img_url     varchar(255)   null,
    work_time   varchar(255)   null,
    price       decimal(10, 2) null,
    total       int default 0  null
);

create table service_area
(
    id         int auto_increment
        primary key,
    admin_id   int               null,
    area_name  varchar(255)      null,
    scope_path text              null,
    region     polygon           not null,
    center_lng decimal(10, 6)    null,
    center_lat decimal(10, 6)    null,
    is_active  tinyint default 0 null
);

create index area_name
    on service_area (area_name);

create spatial index sp_index
    on service_area (region);

create table system_notice
(
    id          int auto_increment
        primary key,
    admin_id    int          null,
    title       varchar(255) null,
    content_url varchar(255) null,
    create_time datetime     null
);

create table user
(
    id              int auto_increment
        primary key,
    username        varchar(16)   null,
    real_name       varchar(16)   null,
    sex             varchar(16)   null,
    birthday        date          null,
    email           varchar(20)   not null,
    password        varchar(100)  not null,
    avatar_url      varchar(255)  null,
    role            int default 0 not null comment '0閿涙碍娅橀柅姘辨暏閹?1閿涙氨顓搁悶鍡楁喅',
    service_status  int           null,
    service_area_id int           null,
    constraint fk_user_area
        foreign key (service_area_id) references service_area (id)
            on update cascade on delete set null
);

create table address
(
    id          int auto_increment
        primary key,
    user_id     int               null,
    contact     varchar(255)      null,
    phone       int               null,
    area        varchar(255)      null,
    detail      varchar(255)      null,
    is_default  tinyint default 0 null,
    lng         decimal(10, 6)    null,
    lat         decimal(10, 6)    null,
    adcode      varchar(6)        null,
    is_verified tinyint           null,
    constraint fk_user_id
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table article
(
    id          varchar(64)  not null
        primary key,
    up_id       int          null,
    title       varchar(255) null,
    content_url varchar(255) null,
    upload_time datetime     null,
    status      int          null,
    constraint fk_user_up
        foreign key (up_id) references user (id)
            on update cascade on delete set null
);

create table binding
(
    id          int auto_increment
        primary key,
    follower_id int         null,
    elder_id    int         null,
    relation    int         null,
    remark      varchar(16) null,
    constraint fk_elder_id
        foreign key (elder_id) references user (id),
    constraint fk_follower_id
        foreign key (follower_id) references user (id)
);

create table emergency_call
(
    id        int auto_increment
        primary key,
    user_id   int          null,
    type      int          null,
    call_time datetime     null,
    remark    varchar(255) null,
    constraint fk_call_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table health_data_bmi
(
    id          int auto_increment
        primary key,
    user_id     int           null,
    height      decimal(5, 2) null,
    weight      decimal(5, 2) null,
    bmi         decimal(5, 2) null,
    record_date datetime      null,
    constraint fk_user_health
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table health_data_bp
(
    id          int auto_increment
        primary key,
    user_id     int      null,
    heart_rate  int      null,
    systolic    int      null,
    diastolic   int      null,
    record_date datetime null,
    constraint fk_bp_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table health_data_bs
(
    id          int auto_increment
        primary key,
    user_id     int           null,
    blood_sugar decimal(4, 2) null,
    meal_status int           null,
    record_date datetime      null,
    constraint fk_bs_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table `order`
(
    id            int auto_increment
        primary key,
    order_sn      varchar(255)   null,
    user_id       int            null,
    staff_id      int            null,
    service_id    int            null,
    service_title varchar(255)   null,
    service_img   varchar(255)   null,
    service_price decimal(10, 2) null,
    lng           decimal(10, 6) null,
    lat           decimal(10, 6) null,
    address_shot  varchar(255)   null,
    phone         varchar(11)    null,
    create_time   datetime       null,
    start_time    datetime       null,
    complete_time datetime       null,
    state         int            null,
    constraint fk_order_service
        foreign key (service_id) references service (id)
            on update cascade on delete set null,
    constraint fk_order_user
        foreign key (user_id) references user (id)
            on update cascade
);

create table safe_area
(
    id         int auto_increment
        primary key,
    user_id    int            null,
    area_name  varchar(255)   null,
    scope_path text           null,
    center_lng decimal(10, 6) null,
    center_lat decimal(10, 6) null,
    is_active  tinyint        null,
    region     polygon        not null,
    constraint fk_area_user
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create spatial index sp_index
    on safe_area (region);

alter table service_area
    add constraint fk_area_admin
        foreign key (admin_id) references user (id)
            on update cascade on delete set null;

create table session
(
    id           int auto_increment
        primary key,
    user_id      int          null,
    target_id    int          null,
    name         varchar(255) null,
    type         int          null,
    last_msg     varchar(255) null,
    unread_count int          null,
    update_time  datetime     null,
    constraint fk_user_session
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

create table staff_work
(
    id         int auto_increment
        primary key,
    staff_id   int null,
    service_id int null,
    constraint fk_staff_service
        foreign key (service_id) references service (id)
            on update cascade on delete cascade,
    constraint fk_staff_user
        foreign key (staff_id) references user (id)
            on update cascade on delete cascade
);



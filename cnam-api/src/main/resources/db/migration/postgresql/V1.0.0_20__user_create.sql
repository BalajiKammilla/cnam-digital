create sequence user_device_seq start with 1 increment by 50;

create sequence user_one_time_password_challenge_seq start with 1 increment by 50;

create sequence user_account_seq start with 1 increment by 50;

create sequence user_role_assignment_seq start with 1 increment by 50;

create sequence user_role_permission_assignment_seq start with 1 increment by 50;

create sequence user_organisation_seq start with 1 increment by 50;

create sequence user_organisational_unit_seq start with 1 increment by 50;

create sequence user_organisational_unit_assignment_seq start with 1 increment by 50;

create sequence user_device_session_seq start with 1 increment by 50;

create table user_device (
    id bigint not null,
    device_identifier varchar(255) not null,
    registered_at timestamp(6) not null,
    constraint pk_user_device primary key (id),
    constraint uc_user_device_identifier unique (device_identifier)
);

create table user_one_time_password_challenge (
    id bigint not null,
    authentication_attempts integer not null,
    challenge_id varchar(255),
    encoded_password varchar(255),
    generated_at timestamp(6),
    maximum_authentication_attempts integer not null,
    alert_channel smallint not null check (alert_channel between 0 and 2),
    sent_at timestamp(6),
    status smallint check (status between 0 and 2),
    user_id bigint not null,
    validity_duration_in_minutes bigint not null,
    constraint pk_user_one_time_password_challenge primary key (id)
);

create table user_account (
    id bigint not null,
    created_at timestamp(6),
    display_name varchar(255),
    email_address varchar(255),
    encoded_password varchar(255),
    last_login_at timestamp(6),
    mobile_number varchar(255),
    alert_channel smallint not null check (alert_channel between 0 and 2),
    realm_name varchar(255) not null,
    status smallint check (status between 0 and 3),
    one_time_password_generator_type varchar(255) not null,
    user_name varchar(255) not null,
    organisation_id bigint,
    constraint pk_user_account primary key (id),
    constraint uc_user_name unique (user_name),
    constraint uc_user_mobile_number unique (mobile_number),
    constraint uc_user_email_address unique (email_address)
);

create table user_permission (
    "value" varchar(255) not null,
    description varchar(255),
    title varchar(255),
    constraint pk_user_permission primary key ("value")
);

create table user_role (
    name varchar(255) not null,
    created_at timestamp(6),
    deleted_at timestamp(6),
    description varchar(255),
    title varchar(255),
    constraint pk_user_role primary key (name)
);

create table user_role_assignment (
    id bigint not null,
    assigned_at timestamp(6),
    role_name varchar(255) not null,
    unassigned_at timestamp(6),
    user_id bigint not null,
    constraint pk_user_role_assignment primary key (id)
);

create table user_role_permission_assignment (
    id bigint not null,
    assigned_at timestamp(6),
    permission_value varchar(255) not null,
    role_name varchar(255) not null,
    unassigned_at timestamp(6),
    constraint pk_user_role_permission_assignment primary key (id)
);
create table user_organisation (
    id bigint not null,
    name varchar(255) not null,
    organisation_number varchar(255) not null,
    status varchar(255) not null check (status in ('ACTIVE','INACTIVE')),
    constraint pk_user_organisation primary key (id),
    constraint uc_user_organisation_number unique (organisation_number)
);
create table user_organisation_address (
    id bigint not null,
    address_line varchar(255) not null,
    city varchar(255) not null,
    country_code varchar(255) not null check (country_code in ('AF','AL','DZ','AS','AD','AO','AI','AQ','AG','AR','AM','AW','AU','AT','AZ','BS','BH','BD','BB','BY','BE','BZ','BJ','BM','BT','BO','BQ','BA','BW','BV','BR','IO','BN','BG','BF','BI','CV','KH','CM','CA','KY','CF','TD','CL','CN','CX','CC','CO','KM','CD','CG','CK','CR','HR','CU','CW','CY','CZ','CI','DK','DJ','DM','DO','EC','EG','SV','GQ','ER','EE','SZ','ET','FK','FO','FJ','FI','FR','GF','PF','TF','GA','GM','GE','DE','GH','GI','GR','GL','GD','GP','GU','GT','GG','GN','GW','GY','HT','HM','VA','HN','HK','HU','IS','IN','ID','IR','IQ','IE','IM','IL','IT','JM','JP','JE','JO','KZ','KE','KI','KP','KR','KW','KG','LA','LV','LB','LS','LR','LY','LI','LT','LU','MO','MG','MW','MY','MV','ML','MT','MH','MQ','MR','MU','YT','MX','FM','MD','MC','MN','ME','MS','MA','MZ','MM','NA','NR','NP','NL','NC','NZ','NI','NE','NG','NU','NF','MP','NO','OM','PK','PW','PS','PA','PG','PY','PE','PH','PN','PL','PT','PR','QA','MK','RO','RU','RW','RE','BL','SH','KN','LC','MF','PM','VC','WS','SM','ST','SA','SN','RS','SC','SL','SG','SX','SK','SI','SB','SO','ZA','GS','SS','ES','LK','SD','SR','SJ','SE','CH','SY','TW','TJ','TZ','TH','TL','TG','TK','TO','TT','TN','TR','TM','TC','TV','UG','UA','AE','GB','UM','US','UY','UZ','VU','VE','VN','VG','VI','WF','EH','YE','ZM','ZW','AX')),
    postal_code varchar(255),
    region varchar(255) not null,
    constraint pk_user_organisation_address primary key (id),
    constraint fk_user_organisation_address_organisation foreign key (id) references user_organisation

);
create table user_organisational_unit_assignment (
    id bigint not null,
    assigned_at timestamp(6) not null,
    unassigned_at timestamp(6),
    organisational_unit_id bigint not null,
    user_account_id bigint not null,
    constraint pk_user_organisational_unit_assignment primary key (id),
    constraint uc_user_organisational_unit_assignment unique (organisational_unit_id, user_account_id)
);
create table user_organisation_contact (
    id bigint not null,
    email_address varchar(255),
    fixed_line_number varchar(255),
    mobile_number varchar(255),
    name varchar(255) not null,
    constraint pk_user_organisation_contact primary key (id),
    constraint fk_user_organisation_contact_organisation foreign key (id) references user_organisation
);
create table user_organisational_unit (
    id bigint not null,
    created_at timestamp(6) not null,
    deleted_at timestamp(6),
    name varchar(255) not null,
    constraint pk_user_organisational_unit primary key (id)
);

create table user_device_session (
    id bigint not null,
    device_identifier varchar(255) not null,
    location_city_code varchar(255),
    location_country_code varchar(255) check (location_country_code in ('AF','AL','DZ','AS','AD','AO','AI','AQ','AG','AR','AM','AW','AU','AT','AZ','BS','BH','BD','BB','BY','BE','BZ','BJ','BM','BT','BO','BQ','BA','BW','BV','BR','IO','BN','BG','BF','BI','CV','KH','CM','CA','KY','CF','TD','CL','CN','CX','CC','CO','KM','CD','CG','CK','CR','HR','CU','CW','CY','CZ','CI','DK','DJ','DM','DO','EC','EG','SV','GQ','ER','EE','SZ','ET','FK','FO','FJ','FI','FR','GF','PF','TF','GA','GM','GE','DE','GH','GI','GR','GL','GD','GP','GU','GT','GG','GN','GW','GY','HT','HM','VA','HN','HK','HU','IS','IN','ID','IR','IQ','IE','IM','IL','IT','JM','JP','JE','JO','KZ','KE','KI','KP','KR','KW','KG','LA','LV','LB','LS','LR','LY','LI','LT','LU','MO','MG','MW','MY','MV','ML','MT','MH','MQ','MR','MU','YT','MX','FM','MD','MC','MN','ME','MS','MA','MZ','MM','NA','NR','NP','NL','NC','NZ','NI','NE','NG','NU','NF','MP','NO','OM','PK','PW','PS','PA','PG','PY','PE','PH','PN','PL','PT','PR','QA','MK','RO','RU','RW','RE','BL','SH','KN','LC','MF','PM','VC','WS','SM','ST','SA','SN','RS','SC','SL','SG','SX','SK','SI','SB','SO','ZA','GS','SS','ES','LK','SD','SR','SJ','SE','CH','SY','TW','TJ','TZ','TH','TL','TG','TK','TO','TT','TN','TR','TM','TC','TV','UG','UA','AE','GB','UM','US','UY','UZ','VU','VE','VN','VG','VI','WF','EH','YE','ZM','ZW','AX')),
    location_district_code varchar(255),
    location_region_code varchar(255),
    latitude float(53),
    longitude float(53),
    model varchar(255) not null,
    network_connection_mode smallint not null check (network_connection_mode between 0 and 7),
    operating_system smallint not null check (operating_system between 0 and 5),
    user_device_id bigint not null,
    started_at timestamp(6) not null,
    user_account_id bigint not null,
    constraint pk_user_device_session primary key (id)
);

alter table if exists user_one_time_password_challenge
   add constraint fk_user_otp_challenge_user
   foreign key (user_id)
   references user_account;

alter table if exists user_role_assignment
   add constraint fk_role_assignment_role
   foreign key (role_name)
   references user_role;

alter table if exists user_role_assignment
   add constraint fk_role_assignment_user
   foreign key (user_id)
   references user_account;

alter table if exists user_role_permission_assignment
   add constraint fk_permission_assignment_permission
   foreign key (permission_value)
   references user_permission;

alter table if exists user_role_permission_assignment
   add constraint fk_permission_assignment_role
   foreign key (role_name)
   references user_role;

alter table if exists user_account
    add constraint fk_user_account_organisation
    foreign key (organisation_id)
    references user_organisation;

alter table if exists user_organisational_unit_assignment
    add constraint fk_ou_assignment_ou
    foreign key (organisational_unit_id)
    references user_organisational_unit;

alter table if exists user_organisational_unit_assignment
    add constraint fk_ou_assignment_user
    foreign key (user_account_id)
    references user_account;

alter table if exists user_device_session
    add constraint fk_user_device_session_device
    foreign key (user_device_id)
    references user_device;

alter table if exists user_device_session
    add constraint fk_user_device_session_user_account
    foreign key (user_account_id)
    references user_account;

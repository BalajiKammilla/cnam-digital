create sequence alert_content_template_seq start with 1 increment by 50;

create sequence alert_outbox_message_result_seq start with 1 increment by 50;

create sequence alert_parameter_seq start with 1 increment by 50;

create sequence alert_seq start with 1 increment by 50;

create sequence alert_template_parameter_seq start with 1 increment by 50;

create sequence alert_template_content_seq start with 1 increment by 50;

create sequence alert_channel_content_template_seq start with 1 increment by 50;

create table alert (
    type varchar(31) not null,
    id bigint not null,
    "language" varchar(255),
    channel smallint check (channel between 0 and 2),
    created_at timestamp(6),
    created_by_id bigint not null,
    expires_at timestamp(6),
    priority smallint check (priority between 0 and 5),
    recipient varchar(255),
    reference varchar(255),
    secondary_reference varchar(255),
    reason varchar(255),
    retain_until timestamp(6),
    sender varchar(255),
    processed_at timestamp(6),
    status smallint check (status between 0 and 1),
    template_id bigint,
    body varchar(4000),
    subject varchar(255),
    batch_id bigint not null,
    send_status smallint check (send_status between 0 and 1),
    read_status smallint check (read_status between 0 and 1),
    read_status_updated_at timestamp(6),
    constraint pk_alert primary key (id)
);

create table alert_outbox_message (
    id bigint not null,
    created_at timestamp(6),
    priority integer not null,
    expires_at timestamp(6),
    failure_count integer not null,
    max_retry_count integer not null,
    last_processed_at timestamp(6),
    next_processing_at timestamp(6),
    processing_id varchar(255),
    last_failure_reason varchar(1000),
    constraint pk_alert_outbox_message primary key (id)
);

create table alert_outbox_message_result (
    id bigint not null,
    alert_id bigint not null,
    alert_message_reference varchar(255),
    failure_error varchar(1000),
    failure_reason varchar(255),
    processed_at timestamp(6) not null,
    processing_id varchar(255) not null,
    status smallint not null check (status between 0 and 3),
    constraint pk_alert_outbox_message_result primary key (id)
);

create table alert_parameter (
    id bigint not null,
    alert_id bigint not null,
    name varchar(255) not null,
    secret boolean not null,
    "value" varchar(1024) not null,
    constraint pk_alert_parameter primary key (id)
);

create table alert_template (
    id bigserial not null,
    created_at timestamp(6) not null,
    created_by_id bigint not null,
    description varchar(255) not null,
    expiry_period_duration bigint,
    expiry_period_unit smallint check (expiry_period_unit between 0 and 6),
    name varchar(255) not null,
    priority smallint not null check (priority between 0 and 5),
    retention_period_duration bigint,
    retention_period_unit smallint check (retention_period_unit between 0 and 4),
    title varchar(255) not null,
    constraint pk_alert_template primary key (id),
    constraint uc_alert_template_name unique (name)
);

create table alert_content_template (
    id bigint not null,
    "name" varchar(255) not null,
    title varchar(255) not null,
    "language" varchar(255) not null,
    sender varchar(255) not null,
    body varchar(4000) not null,
    subject varchar(500),
    constraint pk_alert_content_template primary key (id),
    constraint uc_alert_content_template_name unique (name)
);

create table alert_channel_content_template (
    id bigint not null,
    channel smallint not null check (channel between 0 and 2),
    "language" varchar(255) not null,
    alert_content_template_id bigint not null,
    alert_template_id bigint not null,
    constraint pk_alert_channel_content_template primary key (id),
    constraint uc_alert_channel_content_template unique (alert_template_id, channel, alert_content_template_id)
);

create table alert_template_parameter (
    id bigint not null,
    description varchar(255) not null,
    name varchar(255) not null,
    secret boolean not null,
    template_id bigint not null,
    constraint pk_alert_template_parameter primary key (id),
    constraint uc_alert_template_parameter unique (template_id, name)
);

create view alert_user as select id, user_name, display_name, email_address, mobile_number from user_account;

alter table if exists alert
   add constraint fk_alert_created_by
   foreign key (created_by_id)
   references user_account;

alter table if exists alert
   add constraint fk_alert_template
   foreign key (template_id)
   references alert_template;

alter table if exists alert_outbox_message
   add constraint fk_alert_outbox_message_alert
   foreign key (id)
   references alert;

alter table if exists alert_outbox_message_result
   add constraint fk_alert_outbox_result_alert
   foreign key (alert_id)
   references alert;

alter table if exists alert_parameter
   add constraint fk_alert_parameter_alert
   foreign key (alert_id)
   references alert;

alter table if exists alert_template
   add constraint fk_alert_template_created_by
   foreign key (created_by_id)
   references user_account;

alter table if exists alert_template_parameter
    add constraint fk_alert_template_parameter_template
    foreign key (template_id)
    references alert_template;

alter table if exists alert_channel_content_template
    add constraint fk_alert_channel_content_template_content_template
    foreign key (alert_content_template_id)
    references alert_content_template;

alter table if exists alert_channel_content_template
    add constraint fk_alert_channel_content_template_template
    foreign key (alert_template_id)
    references alert_template;
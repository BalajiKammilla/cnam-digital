create sequence audit_event_seq start with 1 increment by 50;

create table audit_event (
    id bigint not null,
    operation_duration_milliseconds bigint not null,
    operation_name varchar(255),
    operation_status smallint check (operation_status between 0 and 2),
    operation_status_reason varchar(2048),
    resource_id varchar(255),
    resource_name varchar(255),
    trace_id varchar(255),
    triggered_at timestamp(6),
    triggered_by_id bigint,
    device_session_id bigint,
    property1_name varchar(255),
    property1_value varchar(255),
    property2_name varchar(255),
    property2_value varchar(255),
    property3_name varchar(255),
    property3_value varchar(255),
    property4_name varchar(255),
    property4_value varchar(255),
    property5_name varchar(255),
    property5_value varchar(255),
    constraint pk_audit_event primary key (id)
);

alter table if exists audit_event
    add constraint fk_audit_event_device_session
    foreign key (device_session_id)
    references user_device_session;


alter table if exists audit_event
    add constraint fk_audit_event_triggered_by
    foreign key (triggered_by_id)
    references user_account;

create view audit_device_session as
    select
        id,
        started_at,
        device_identifier,
        location_city_code,
        location_country_code,
        location_district_code,
        location_region_code,
        latitude,
        longitude,
        model,
        network_connection_mode,
        operating_system
    from user_device_session;

create view audit_user as
    select
        id,
        display_name,
        user_name
    from user_account;
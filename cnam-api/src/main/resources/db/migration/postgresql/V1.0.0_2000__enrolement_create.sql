create sequence dedupe_match_seq start with 1 increment by 50;

create sequence enrolment_outbox_task_result_seq start with 1 increment by 50;

create sequence enrolment_outbox_task_seq start with 1 increment by 50;

create sequence partial_enrolment_seq start with 1 increment by 50;

create sequence supporting_document_seq start with 1 increment by 50;

create sequence enrolment_action_seq start with 1 increment by 50;

create sequence corrective_action_seq start with 1 increment by 50;

create sequence verification_outbox_task_result_seq start with 1 increment by 50;

create sequence verification_outbox_task_seq start with 1 increment by 50;

create sequence review_task_seq start with 1 increment by 50;

create table dedupe_match (
    id bigint not null,
    date_of_birth date not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    identity_document_number varchar(255) not null,
    identity_document_type_code varchar(255) not null,
    constraint pk_dedupe_match primary key (id)
);

create table enrolment_details (
    id bigint not null,
    agency_code varchar(255),
    sub_prefecture_code varchar(255),
    certificate_issue_date date,
    certificate_number varchar(255),
    country_code varchar(255),
    "date" date,
    birth_sub_prefecture_code varchar(255),
    email varchar(255),
    landline_number varchar(255),
    mobile_number varchar(255),
    post_office_box varchar(255),
    current_nationality_code varchar(255),
    first_names varchar(255),
    last_name varchar(255),
    maiden_name varchar(255),
    marital_status_code varchar(255),
    title_code varchar(255),
    employer_company_code varchar(255),
    person_type_code varchar(255),
    profession_code varchar(255),
    registration_number varchar(255),
    registration_number_type_code varchar(255),
    paid_by_code varchar(255),
    cnam_number varchar(255),
    enrolment_id varchar(255),
    payer_first_name varchar(255),
    payer_last_name varchar(255),
    constraint pk_enrolment_details primary key (id)
);

create table enrolment_outbox_task (
    id bigint not null,
    created_at timestamp(6) not null,
    failure_count integer not null,
    last_failure_reason varchar(255),
    last_processed_at timestamp(6),
    page_number integer not null,
    processing_id varchar(255),
    sequence_number integer not null,
    enrolment_id bigint not null,
    identity_document_id bigint,
    supporting_document_id bigint,
    constraint pk_enrolment_outbox_task primary key (id)
);

create table enrolment_outbox_task_result (
    id bigint not null,
    completed_at timestamp(6) not null,
    failure_error varchar(4000),
    failure_reason varchar(1000),
    page_number integer not null,
    processing_id varchar(255) not null,
    sequence_number integer not null,
    started_at timestamp(6) not null,
    enrolment_id bigint not null,
    identity_document_id bigint,
    supporting_document_id bigint,
    constraint pk_enrolment_outbox_task_result primary key (id)
);

create table identity_document (
    id bigint not null,
    document_attachment_id varchar(255) not null,
    document_number varchar(255) not null,
    document_type_code varchar(255) not null,
    page_count integer not null,
    ocr_succeeded boolean not null,
    constraint pk_identity_document primary key (id),
    constraint uc_identity_document_attachment_id unique (document_attachment_id)
);

create table partial_enrolment (
    id bigint not null,
    approval_status smallint check (approval_status between 0 and 2),
    verification_task_pending_count integer not null,
    review_task_pending_count integer not null,
    corrective_action_pending_count integer not null,
    verified_at timestamp(6),
    completed_at timestamp(6),
    dedupe_match_id bigint,
    enrolment_id varchar(255) not null unique,
    external_reference varchar(255),
    fingerprints_attachment_id varchar(255),
    mobile_number varchar(255) not null,
    photo_attachment_id varchar(255),
    processed_at timestamp(6),
    signature_attachment_id varchar(255),
    started_at timestamp(6) not null,
    identity_document_ocr_succeeded boolean,
    version integer not null,
    constraint pk_partial_enrolment primary key (id),
    constraint uc_partial_enrolment_id unique (enrolment_id)
);

create table supporting_document (
    id bigint not null,
    document_attachment_id varchar(255) not null,
    document_type_code varchar(255) not null,
    enrolment_id bigint not null,
    page_count integer not null,
    purpose smallint not null check (purpose between 0 and 2),
    constraint pk_supporting_document primary key (id),
    constraint uc_supporting_document_attachment_id unique (document_attachment_id)
);

create table enrolment_action (
    id bigint not null,
    attachment_id varchar(255),
    created_at timestamp(6) not null,
    kind smallint not null check (kind between 0 and 9),
    trace_id varchar(255) not null,
    device_session_id bigint,
    enrolment_id bigint not null,
    constraint pk_enrolment_action primary key (id)
);

create table corrective_action (
    id bigint not null,
    completed_at timestamp(6),
    created_at timestamp(6) not null,
    status smallint check (status between 0 and 1),
    type smallint not null check (type between 0 and 1),
    enrolment_id bigint not null,
    alert_id bigint not null,
    constraint pk_corrective_action primary key (id)
);

create table verification_outbox_task (
    id bigint not null,
    type smallint not null check (type between 0 and 2),
    status smallint not null check (status between 0 and 2),
    enrolment_id bigint not null,
    created_at timestamp(6) not null,
    last_processed_at timestamp(6),
    failure_count integer not null,
    max_retry_count integer not null,
    expires_at timestamp(6),
    next_processing_at timestamp(6),
    priority integer not null,
    processing_id varchar(255),
    last_failure_reason varchar(1000),
    constraint pk_verification_outbox_task primary key (id)
);

create table verification_outbox_task_result (
    id bigint not null,
    processing_id varchar(255),
    type smallint not null check (type between 0 and 2),
    status smallint not null check (status between 0 and 2),
    task_id bigint not null,
    enrolment_id bigint not null,
    processed_at timestamp(6) not null,
    failure_reason varchar(255),
    failure_error varchar(1024),
    constraint pk_verification_outbox_task_result primary key (id)
);

create table review_task (
    type varchar(30) not null,
    id bigint not null,
    enrolment_id bigint not null,
    created_at timestamp(6) not null,
    status smallint not null check (status between 0 and 2),
    completed_at timestamp(6),
    completed_by_id bigint,
    note varchar(1000),
    dedupe_id bigint,
    constraint pk_review_task primary key (id),
    constraint uc_review_task_dedupe_enrolment unique (dedupe_id, enrolment_id)
);

create view enrolment_user
    as select id, realm_name, user_name, display_name, email_address, mobile_number
       from user_account;

create view enrolment_device_session
as select
       id,
       device_identifier,
       location_city_code,
       location_country_code,
       location_district_code,
       location_region_code,
       latitude,
       longitude,
       model,
       network_connection_mode,
       operating_system,
       started_at,
       user_device_id,
       user_account_id
   from user_device_session;

alter table if exists enrolment_details
   add constraint fk_enrolment_details
   foreign key (id)
   references partial_enrolment;

alter table if exists enrolment_outbox_task
   add constraint fk_enrolment_outbox_enrolment
   foreign key (enrolment_id)
   references partial_enrolment;

alter table if exists enrolment_outbox_task
   add constraint fk_enrolment_outbox_identity_document
   foreign key (identity_document_id)
   references identity_document;

alter table if exists enrolment_outbox_task
   add constraint fk_enrolment_outbox_supporting_document
   foreign key (supporting_document_id)
   references supporting_document;

alter table if exists enrolment_outbox_task_result
   add constraint fk_enrolment_outbox_enrolment
   foreign key (enrolment_id)
   references partial_enrolment;

alter table if exists enrolment_outbox_task_result
   add constraint fk_enrolment_outbox_identity_document
   foreign key (identity_document_id)
   references identity_document;

alter table if exists enrolment_outbox_task_result
   add constraint fk_enrolment_outbox_supporting_document
   foreign key (supporting_document_id)
   references supporting_document;

alter table if exists identity_document
   add constraint fk_identity_document_enrolment
   foreign key (id)
   references partial_enrolment;

alter table if exists partial_enrolment
   add constraint fk_enrolment_dedupe_match
   foreign key (dedupe_match_id)
   references dedupe_match;

alter table if exists supporting_document
   add constraint fk_supporting_document_enrolment
   foreign key (enrolment_id)
   references partial_enrolment;

alter table if exists enrolment_action
    add constraint fk_enrolment_action_device_session
    foreign key (device_session_id)
    references user_device_session;

alter table if exists enrolment_action
    add constraint fk_enrolment_action_enrolment
    foreign key (enrolment_id)
    references partial_enrolment;

alter table if exists corrective_action
    add constraint fk_corrective_action_enrolment
    foreign key (enrolment_id)
    references partial_enrolment;

alter table if exists verification_outbox_task
    add constraint fk_verification_outbox_task_enrolment
    foreign key (enrolment_id)
    references partial_enrolment;

alter table if exists verification_outbox_task_result
    add constraint fk_verification_outbox_task_result_task
    foreign key (task_id)
    references verification_outbox_task;

alter table if exists verification_outbox_task_result
    add constraint fk_verification_outbox_task_result_enrolment
    foreign key (enrolment_id)
    references partial_enrolment;

alter table if exists review_task
    add constraint fk_review_task_completed_by
    foreign key (completed_by_id)
    references user_account;

alter table if exists review_task
    add constraint fk_review_task_enrolment
    foreign key (enrolment_id)
    references partial_enrolment;

alter table if exists review_task
    add constraint fk_review_task_dedupe
    foreign key (dedupe_id)
    references dedupe_match;
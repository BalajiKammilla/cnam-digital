create table configuration_property (
    name varchar(255) not null,
    type varchar(255) not null check (type in ('BOOLEAN','INTEGER','STRING', 'ENUM', 'CHARACTER_LIST')),
    "value" varchar(255),
    constraint pk_configuration_property primary key (name)
)
;

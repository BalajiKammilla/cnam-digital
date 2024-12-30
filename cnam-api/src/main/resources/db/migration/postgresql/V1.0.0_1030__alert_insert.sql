insert into alert_template (
    id, priority, created_at, created_by_id, "name", title, description
) values (
    nextval('public.alert_template_id_seq'), 5, current_timestamp,
    (select id from alert_user where user_name = 'info@ipscnam.ci'),
    'ONE_TIME_PASSWORD',
    'One Time Password Alert',
    'One time password alert'
), (
    nextval('public.alert_template_id_seq'), 4, current_timestamp,
    (select id from alert_user where user_name = 'info@ipscnam.ci'),
    'ENROLMENT_COMPLETED',
    'Enrolment Completed',
    'Enrolment completed'
), (
    nextval('public.alert_template_id_seq'), 4, current_timestamp,
    (select id from alert_user where user_name = 'info@ipscnam.ci'),
    'CORRECTIVE_ACTION_REQUIRED',
    'Corrective Action Required',
    'Corrective action required'
), (
     nextval('public.alert_template_id_seq'), 4, current_timestamp,
     (select id from alert_user where user_name = 'info@ipscnam.ci'),
     'ENROLMENT_REJECTED',
     'Enrolment Rejected',
     'Enrolment Rejected'
 ), (
     nextval('public.alert_template_id_seq'), 4, current_timestamp,
     (select id from alert_user where user_name = 'info@ipscnam.ci'),
     'ENROLMENT_VERIFIED',
     'Enrolment Verified',
     'Enrolment Verified'
 )
;

insert into alert_content_template (id, "name", title, language, sender, subject, body)
values (
    nextval('public.alert_content_template_seq'),
    'ONE_TIME_PASSWORD_EMAIL_FR', -- name
    'One time password email (FR)', -- title
    'FR', -- language
    'CMU Mobile <info@ipscnam.ci>', -- sender
    'Code de Validation', -- subject
    'Votre code de validation est <OTP>'
), (
    nextval('public.alert_content_template_seq'),
    'ONE_TIME_PASSWORD_SMS_FR', -- name
    'One time password SMS (FR)', -- title
    'FR', -- language
    'CNAM', -- sender
    null, -- subject
    'Votre code de validation est <OTP>'
), (
    nextval('public.alert_content_template_seq'),
    'ENROLMENT_COMPLETED_FR', -- name
    'Enrolment completed (FR)', -- title
    'FR', -- language
    'CNAM', -- sender
    null, -- subject
    'Nous avons reçu votre demande d''enrôlement. Nous effectuons quelques vérifications sur les données transmises à l’issue desquelles nous vous enverrons un message.'
), (
    nextval('public.alert_content_template_seq'),
    'CORRECTIVE_ACTION_REQUIRED_FR', -- name
    'Corrective action required (FR)', -- title
    'FR', -- language
    'CNAM', -- sender
    null, -- subject
    'Votre demande d''enrôlement n’a pu être enregistrée: <REASON>. Veuillez corriger l’anomalie ou refaire une autre demande.'
), (
   nextval('public.alert_content_template_seq'),
   'ENROLMENT_REJECTED_FR', -- name
   'Enrolment rejected (FR)', -- title
   'FR', -- language
   'CNAM', -- sender
   null, -- subject
   'Votre demande d''enrôlement n’a pu être enregistrée: <REASON>. Veuillez refaire une autre demande.'
), (
   nextval('public.alert_content_template_seq'),
   'ENROLMENT_VERIFIED_FR', -- name
   'Enrolment verified (FR)', -- title
   'FR', -- language
   'CNAM', -- sender
   null, -- subject
   'Votre demande d''enrôlement CMU a été soumise avec succès pour vérifications. Votre numéro d''enrôlement est: <ENROLMENT_ID>'
)
;

insert into alert_channel_content_template (id, channel, language, alert_template_id, alert_content_template_id)
values (
    nextval('public.alert_channel_content_template_seq'),
    2, 'FR',
    (select id from alert_template where "name" = 'ONE_TIME_PASSWORD'),
    (select id from alert_content_template where "name" = 'ONE_TIME_PASSWORD_EMAIL_FR')
),  (
    nextval('public.alert_channel_content_template_seq'),
    1, 'FR',
    (select id from alert_template where "name" = 'ONE_TIME_PASSWORD'),
    (select id from alert_content_template where "name" = 'ONE_TIME_PASSWORD_SMS_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    0, 'FR',
    (select id from alert_template where "name" = 'ENROLMENT_COMPLETED'),
    (select id from alert_content_template where "name" = 'ENROLMENT_COMPLETED_FR')
), (
   nextval('public.alert_channel_content_template_seq'),
   1, 'FR',
   (select id from alert_template where "name" = 'ENROLMENT_COMPLETED'),
   (select id from alert_content_template where "name" = 'ENROLMENT_COMPLETED_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    0, 'FR',
    (select id from alert_template where "name" = 'CORRECTIVE_ACTION_REQUIRED'),
    (select id from alert_content_template where "name" = 'CORRECTIVE_ACTION_REQUIRED_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    1, 'FR',
   (select id from alert_template where "name" = 'CORRECTIVE_ACTION_REQUIRED'),
   (select id from alert_content_template where "name" = 'CORRECTIVE_ACTION_REQUIRED_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    0, 'FR',
   (select id from alert_template where "name" = 'ENROLMENT_REJECTED'),
   (select id from alert_content_template where "name" = 'ENROLMENT_REJECTED_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    1, 'FR',
   (select id from alert_template where "name" = 'ENROLMENT_REJECTED'),
   (select id from alert_content_template where "name" = 'ENROLMENT_REJECTED_FR')
), (
    nextval('public.alert_channel_content_template_seq'),
    1, 'FR',
   (select id from alert_template where "name" = 'ENROLMENT_VERIFIED'),
   (select id from alert_content_template where "name" = 'ENROLMENT_VERIFIED_FR')
)
;

insert into alert_template_parameter (id, name, description, secret, template_id) values
(nextval('public.alert_template_parameter_seq'), 'OTP', 'One time password', true,
    (select id from alert_template where "name" = 'ONE_TIME_PASSWORD')),
(nextval('public.alert_template_parameter_seq'), 'ENROLMENT_ID', 'Enrolment number', false,
    (select id from alert_template where "name" = 'ENROLMENT_COMPLETED')),
(nextval('public.alert_template_parameter_seq'), 'REASON', 'Corrective action reason', false,
    (select id from alert_template where "name" = 'CORRECTIVE_ACTION_REQUIRED')),
(nextval('public.alert_template_parameter_seq'), 'REASON', 'Enrolment rejection reason', false,
 (select id from alert_template where "name" = 'ENROLMENT_REJECTED'))
;

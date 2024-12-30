insert into user_permission ("value", title, description) values
('permission:list', 'List Permissions', 'List permissions'),
('role:create', 'Create Role', 'Create role'),
('role:update', 'Update Role', 'Update role'),
('role:delete', 'Delete Role', 'Delete role'),
('role:list', 'List Roles', 'List roles'),
('user:create', 'Create User', 'Create user'),
('user:read', 'View User', 'Read user'),
('user:update', 'Update User', 'Update user'),
('user:list', 'List Users', 'List users'),
('device:read', 'View Device', 'View device'),
('alert:create', 'Create Alert', 'Create alert'),
('alert:list', 'List Alert', 'List alert'),
('partner:create', 'Create Partner', 'Create partner'),
('partner:read', 'View Partner', 'Read partner'),
('partner:update', 'Update Partner', 'Update partner'),
('partner:list', 'List Partners', 'List partners'),
('enrolment:read', 'View Enrolment', 'Read enrolment'),
('enrolment:update', 'Update Enrolment', 'Update enrolment'),
('enrolment:approve', 'Approve Enrolment', 'Approve enrolment'),
('enrolment:list', 'List Enrolments', 'List enrolments'),
('dedupe:list', 'List Dedupe Matches', 'List dedupe matches'),
('dashboard:read', 'View Dashboard', 'View dashboard'),
('content:update', 'Update Content', 'Update content (i.e.: banner)')
;

insert into user_role (name, title, description, created_at, deleted_at) values
('USER_MANAGER', 'User Manager', 'Manage back office and assisted-enrolment users', current_timestamp, null),
('ENROLMENT_ADJUDICATOR', 'Enrolment Adjudicator', 'Adjudicate submitted of enrolments', current_timestamp, null),
('REPORT_VIEWER', 'Report Viewer', 'View the reporting dashboard', current_timestamp, null),
('CONTENT_MANAGER', 'Content Manager', 'Update banner content', current_timestamp, null)
;

insert into user_role_permission_assignment (id, role_name, permission_value, assigned_at, unassigned_at) values
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'permission:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'role:create', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'role:update', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'role:delete', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'role:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'user:create', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'user:read', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'user:update', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'user:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'partner:create', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'partner:read', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'partner:update', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'partner:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'USER_MANAGER', 'device:read', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'enrolment:read', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'enrolment:update', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'enrolment:approve', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'enrolment:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'dedupe:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'alert:create', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'ENROLMENT_ADJUDICATOR', 'alert:list', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'REPORT_VIEWER', 'dashboard:read', current_timestamp, null),
(nextval('public.user_role_permission_assignment_seq'), 'CONTENT_MANAGER', 'content:update', current_timestamp, null)
;

insert into user_account (id, status, user_name, display_name, email_address, mobile_number, realm_name, encoded_password, created_at, last_login_at, one_time_password_generator_type, alert_channel)
values (nextval('public.user_account_seq'), 2, 'info@ipscnam.ci', 'CMU Mobile', 'info@ipscnam.ci', null, 'system', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'TOTP', 2)
;

insert into user_account (id, status, user_name, display_name, email_address, mobile_number, realm_name, encoded_password, created_at, last_login_at, one_time_password_generator_type, alert_channel) values
(nextval('public.user_account_seq'), 2, 'support@aptiway.com', 'Aptiway Support', 'support@aptiway.com', null, 'cnam', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'FIXED', 2),
(nextval('public.user_account_seq'), 2, 'test@aptiway.com', 'Test', 'test@aptiway.com', null, 'cnam', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'FIXED', 2),
(nextval('public.user_account_seq'), 2, 'narcisse@groupedigital.com', 'Narcisse', 'narcisse@groupedigital.com', null, 'cnam', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'alberto@groupedigital.com', 'Alberto', 'alberto@groupedigital.com', null, 'cnam', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'prabhakar@aptiway.com', 'Prabhakar', 'prabhakar@aptiway.com', null, 'cnam', '$2b$11$24o0uVztdeAn53AptY9oZu9rv7T5YEZSyycgFrQB0FZVjeGKSnEOa', current_timestamp, null, 'FIXED', 2),

(nextval('public.user_account_seq'), 2, 'jean-jacques.coulibaly@ipscnam.ci', 'Jean Jacques Coulibaly', 'jean-jacques.coulibaly@ipscnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'fulgence.yao@cnam.ci', 'Fulgence Yao', 'fulgence.yao@cnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'josue.bobou@cnam.ci', 'Josue Bobou', 'josue.bobou@cnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'issiaka.sylla@technchange.net', 'Issiaka Sylla', 'issiaka.sylla@technchange.net', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'madi.sako@cnam.ci', 'Madi Sako', 'madi.sako@cnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'innocent.oussou@cnam.ci', 'Innocent Oussou', 'innocent.oussou@cnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'nawa.sekongo@technchange.net', 'Nawa Sekongo', 'nawa.sekongo@technchange.net', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'ahmed.diomande@ipscnam.ci', 'Ahmed Diomande', 'ahmed.diomande@ipscnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'cheickna.doumbia@cnam.ci', 'Cheickna Doumbia', 'cheickna.doumbia@cnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'yves.mao@technchange.net', 'Yves Mao', 'yves.mao@technchange.net', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2),
(nextval('public.user_account_seq'), 2, 'kartya.kone@ipscnam.ci', 'Kartya Kone', 'kartya.kone@ipscnam.ci', null, 'cnam', '$2b$11$TnZSzX.MIeDaK2YLwz4..OxuLuaX/IGQY.WhTpEmTMi8FF5DOpL6G', current_timestamp, null, 'TOTP', 2)
;

insert into user_role_assignment (id, user_id, role_name, assigned_at, unassigned_at) values
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'support@aptiway.com'), 'USER_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'support@aptiway.com'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'support@aptiway.com'), 'REPORT_VIEWER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'support@aptiway.com'), 'CONTENT_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'prabhakar@aptiway.com'), 'USER_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'prabhakar@aptiway.com'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'prabhakar@aptiway.com'), 'REPORT_VIEWER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'prabhakar@aptiway.com'), 'CONTENT_MANAGER', current_timestamp, null),

(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'jean-jacques.coulibaly@ipscnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'fulgence.yao@cnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'josue.bobou@cnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'issiaka.sylla@technchange.net'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'madi.sako@cnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'innocent.oussou@cnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'nawa.sekongo@technchange.net'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'ahmed.diomande@ipscnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'cheickna.doumbia@cnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'yves.mao@technchange.net'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'kartya.kone@ipscnam.ci'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null)
;

insert into user_role_assignment (id, user_id, role_name, assigned_at, unassigned_at) values
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'test@aptiway.com'), 'USER_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'test@aptiway.com'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'test@aptiway.com'), 'REPORT_VIEWER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'test@aptiway.com'), 'CONTENT_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'narcisse@groupedigital.com'), 'USER_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'narcisse@groupedigital.com'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'narcisse@groupedigital.com'), 'REPORT_VIEWER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'narcisse@groupedigital.com'), 'CONTENT_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'alberto@groupedigital.com'), 'USER_MANAGER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'alberto@groupedigital.com'), 'ENROLMENT_ADJUDICATOR', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'alberto@groupedigital.com'), 'REPORT_VIEWER', current_timestamp, null),
(nextval('public.user_role_assignment_seq'), (select id from user_account where user_name = 'alberto@groupedigital.com'), 'CONTENT_MANAGER', current_timestamp, null)
;

insert into user_account (id, status, user_name, display_name, email_address, mobile_number, realm_name, encoded_password, created_at, last_login_at, one_time_password_generator_type, alert_channel) values
(nextval('public.user_account_seq'), 2, '+2250167111111', 'App Store Verification User', null, '+2250167111111', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'FIXED', 1),
(nextval('public.user_account_seq'), 2, '+2250167222222', 'Test User # 2', null, '+2250167222222', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'FIXED', 1),
(nextval('public.user_account_seq'), 2, '+2250187901407', 'Prabhakar', null, '+2250187901407', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'FIXED', 1),
(nextval('public.user_account_seq'), 2, '+22567169471', 'Narcisse', null, '+22567169471', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250505575715', 'Alberto', null, '+2250505575715', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250779789510', 'Madi SAKO', null, '+2250779789510', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250777087215', 'Dognimin KOULIBALI', null, '+2250777087215', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250767194977', 'Bobou Josue', null, '+2250767194977', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250707820138', 'Fabrice BILE', null, '+2250707820138', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250707677270', 'Oussou Kouame', null, '+2250707677270', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250777079293', 'KANATE', null, '+2250777079293', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250757422457', 'CONGO', null, '+2250757422457', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250757377819', 'Edi Charles', null, '+2250757377819', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250708017183', 'DJENEBA Ouattara', null, '+2250708017183', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250789752389', 'Mme Nawa Sekongo', null, '+2250789752389', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250778992531', 'M Yoane Gbe', null, '+2250778992531', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1),
(nextval('public.user_account_seq'), 2, '+2250707515962', 'M Issiaka Sylla', null, '+2250707515962', 'public', '$2b$11$zUVE62NDFI0mSpP6Gjc8XuwMvb2zzyxD053KKSTylnMXnCr.xN5OC', current_timestamp, null, 'TOTP', 1)
;

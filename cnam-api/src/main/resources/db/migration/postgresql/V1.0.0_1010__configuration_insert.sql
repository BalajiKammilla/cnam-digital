insert into configuration_property (name, type, "value") values
-- user-name-type
('user.policy.cnam.user-name-type', 'ENUM', 'EMAIL'),
('user.policy.partner.user-name-type', 'ENUM', 'MOBILE'),
('user.policy.public.user-name-type', 'ENUM', 'MOBILE'),
-- alert-channel
('user.policy.cnam.alert-channel', 'ENUM', 'EMAIL'),
('user.policy.partner.alert-channel', 'ENUM', 'SMS'),
('user.policy.public.alert-channel', 'ENUM', 'SMS'),
-- alert-channel-verification-required
('user.policy.partner.alert-channel-verification-required', 'BOOLEAN', 'true'),
('user.policy.public.alert-channel-verification-required', 'BOOLEAN', 'true'),
-- device-metadata-required
('user.policy.partner.device-metadata-required', 'BOOLEAN', 'true'),
('user.policy.public.device-metadata-required', 'BOOLEAN', 'true'),
-- password.policy
('user.password.policy.public.minimum-length', 'INTEGER', '5'),
('user.password.policy.public.maximum-length', 'INTEGER', '10'),
('user.password.policy.public.digits-characters', 'STRING', 'REQUIRED'),
('user.password.policy.partner.minimum-length', 'INTEGER', '5'),
('user.password.policy.partner.maximum-length', 'INTEGER', '10'),
('user.password.policy.partner.digits-characters', 'STRING', 'REQUIRED'),
-- device-inactivity
('user.otp.policy.partner.device-inactivity-duration', 'INTEGER', '60'),
('user.otp.policy.partner.device-inactivity-unit', 'ENUM', 'DAYS'),
('user.otp.policy.public.device-inactivity-duration', 'INTEGER', '60'),
('user.otp.policy.public.device-inactivity-unit', 'ENUM', 'DAYS')
;

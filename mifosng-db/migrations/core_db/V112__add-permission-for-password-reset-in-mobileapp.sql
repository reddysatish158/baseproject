insert ignore into m_permission values  (null,'organisation', 'CREATE_SMTPCONFIGURATION', 'SMTPCONFIGURATION', 'CREATE', 0); 

insert ignore into m_permission values  (null,'organisation', 'UPDATE_SELFCAREUDP', 'SELFCAREUDP', 'UPDATE', 0); 

insert ignore into m_permission values  (null,'organisation', 'CREATE_SMTPCONFIGURATION', 'SMTPCONFIGURATION', 'UPDATE', 0);

insert ignore into m_permission values  (null,'organisation', 'CREATE_SELFCAREUDP', 'SELFCAREUDP', 'CREATE', 0); 

insert ignore into m_permission  VALUES (null,'billing','UPDATE_MEDIADEVICE','MEDIADEVICE','UPDATE',0);

INSERT ignore INTO `b_charge_codes`(charge_code,charge_description,charge_type,charge_duration,duration_type,tax_inclusive,billfrequency_code) VALUES ('NONE','NONE','0',0,'0',0,'0');

insert ignore into `c_configuration` (`id`,`name`,`enabled`,`value`) values (null,'Active Viewers',1,'2');

insert ignore into `c_configuration` values(null,'Is_Paypal_For_Ios',1,'');

insert ignore into m_permission  VALUES (null,'billing','MAIL_SELFCAREUDP','SELFCAREUDP','UPDATE',0);
CREATE  TABLE if not exists `b_clientuser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` varchar(10) NOT NULL,
  `unique_reference` varchar(100) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `unique_reference` (`unique_reference`),
  KEY `FK_ClientId` (`client_id`),
  CONSTRAINT `FK_ClientId` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);

Drop procedure IF EXISTS addstatus; 
DELIMITER //
create procedure addstatus() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_clientuser add column status varchar(50) after unique_reference;

END IF;
END //
DELIMITER ;
call addstatus();

Drop procedure IF EXISTS addstatus;



DELIMITER $$
Drop procedure IF EXISTS addoffice_type $$
create procedure addoffice_type() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_type'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_office add column office_type int(10) NOT NULL DEFAULT '117';
END IF;
END $$
DELIMITER ;
call addoffice_type();
Drop procedure IF EXISTS addoffice_type;

DELIMITER $$
Drop procedure IF EXISTS addusername $$
create procedure addusername() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'username'
     and TABLE_NAME = 'b_login_history'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_login_history add column username varchar(50) DEFAULT NULL;
END IF;
END $$
DELIMITER ;
call addusername();
Drop procedure IF EXISTS addusername;


CREATE TABLE if not exists `m_document` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `parent_entity_type` varchar(50) NOT NULL,
  `parent_entity_id` int(20) NOT NULL DEFAULT '0',
  `name` varchar(250) NOT NULL,
  `file_name` varchar(250) NOT NULL,
  `size` int(20) DEFAULT '0',
  `type` varchar(500) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `location` varchar(500) NOT NULL DEFAULT '0',
  `storage_type_enum` smallint(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
); 

CREATE TABLE if not exists  `b_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) DEFAULT NULL,
  `group_address` varchar(100) DEFAULT NULL,
  `group_description` varchar(100) DEFAULT NULL,
  `attribute1` varchar(50) DEFAULT NULL,
  `attribute2` varchar(50) DEFAULT NULL,
  `attribute3` varchar(50) DEFAULT NULL,
  `attribute4` varchar(50) DEFAULT NULL,
  `attribute5` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) 

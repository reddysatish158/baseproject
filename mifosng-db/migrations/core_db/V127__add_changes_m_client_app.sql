insert ignore into m_permission values  (null,'organisation', 'ACTIVATIONPROCESS_ACTIVATE', 'ACTIVATIONPROCESS', 'CREATE', 0); 

Drop procedure IF EXISTS addm_client; 
DELIMITER //
create procedure addm_client() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'group_id'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table m_client add column group_id bigint(10) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call addm_client();

Drop procedure IF EXISTS addm_client;
DELIMITER //
create procedure addm_client() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'exempt_tax'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table m_client add column exempt_tax char(1) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call addm_client();

Drop procedure IF EXISTS addm_client;
DELIMITER //
create procedure addm_client() 
Begin

IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_indororp'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table m_client add column is_indororp varchar(3) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call addm_client();

Drop procedure IF EXISTS addm_client;


DELIMITER //
create procedure addm_client() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'bill_mode'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table m_client add column bill_mode varchar(10) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call addm_client();

Drop procedure IF EXISTS addm_client;

DELIMITER //
create procedure addm_client() 
Begin
IF EXISTS (SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE
`TABLE_CATALOG` = 'def' AND `TABLE_SCHEMA` = DATABASE() AND
`TABLE_NAME` = 'm_client' AND `INDEX_NAME` = 'email_key')THEN
alter table m_client drop index email_key;
END IF;

END //
DELIMITER ;
call addm_client();

Drop procedure IF EXISTS addm_client;


DELIMITER $$
Drop procedure IF EXISTS addservicetype $$
create procedure addservicetype() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_type'
     and TABLE_NAME = 'b_process_request_detail'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_process_request_detail  
add column service_type varchar (20) DEFAULT NULL;
END IF;
END $$
DELIMITER ;
call addservicetype();

insert IGNORE into m_permission values(null, 'portfolio', 'UPDATE_CLIENTBILLMODE', 'CLIENTBILLMODE', 'UPDATE', '0');

Drop procedure IF EXISTS addservicetype;

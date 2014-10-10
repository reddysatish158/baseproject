Drop procedure IF EXISTS addb_onetime_sale; 
DELIMITER //
create procedure addb_onetime_sale() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_id'
     and TABLE_NAME = 'b_onetime_sale'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_onetime_sale add column office_id int(20) default NULL;
END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'contract_period'
     and TABLE_NAME = 'b_onetime_sale'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_onetime_sale add column contract_period bigint(10) default NULL;

END IF;
END //
DELIMITER ;
call addb_onetime_sale();
Drop procedure IF EXISTS addb_onetime_sale;


Drop procedure IF EXISTS add_sale;
DELIMITER //
create procedure add_sale() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'device_mode'
     and TABLE_NAME = 'b_onetime_sale'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_onetime_sale add column device_mode varchar(20) default NULL;

END IF;

END //
DELIMITER ;
call add_sale();

Drop procedure IF EXISTS add_sale;


insert ignore into m_permission VALUES (null,'ordering','TERMINATE_ORDER','ORDER','TERMINATE',0);

insert ignore into c_configuration values (0,'constraint_approach_for_datatables',0,Null);

delete from r_enum_value where enum_name in ('amortization_method_enum',
'interest_calculated_in_period_enum','interest_method_enum','interest_period_frequency_enum','loan_status_id','loan_transaction_strategy_id',
'repayment_period_frequency_enum','term_period_frequency_enum','transaction_type_enum');

    
Drop procedure IF EXISTS addcode_value; 
DELIMITER //
create procedure addcode_value() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'code_value'
     and TABLE_NAME = 'm_code_value'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_code_value add code_value int(20);

END IF;
  
END //
DELIMITER ;
call addcode_value();

Drop procedure IF EXISTS addcode_value;


Drop procedure IF EXISTS addcode_value; 
DELIMITER //
create procedure addcolumntoconf() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'value'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
alter table c_configuration modify column value varchar(150) default null;

END IF;
  
END //
DELIMITER ;
call addcolumntoconf();

Drop procedure IF EXISTS addcolumntoconf;

INSERT ignore INTO `c_configuration` VALUES (null,'SMTP',0,'{\"mailId\":\"test@gmail.com\",\"password\":\"byMjk=\",\"hostName\":\"smtp.gmail.com\",\"port\":\"\"}');

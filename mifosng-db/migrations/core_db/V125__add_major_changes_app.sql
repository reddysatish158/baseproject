update job set name ='Requestor' where display_name='Requestor';

update job set name ='Invoice' where display_name='Invoice';

update job set name ='Messanger' where display_name='Messanger';

update job set name ='Auto Exipiry' where display_name='Auto Exipiry';

update job set name ='EventAction Processor' where display_name='EventAction Processor';

update job set name ='PushNotification' where display_name='PushNotification';

update job set name ='Middleware' where display_name='Middleware Integration';

update job set name ='Report Email' where display_name='Report Email';

update job set name ='Generate Statement' where display_name='Generate Statement';
update job set name ='Generate PDF' where display_name='Generate PDF';
Update r_enum_value set enum_message_property='Exact Prorata' where enum_name='billing_rules' and enum_id=100;
Update r_enum_value set enum_message_property='Full Month' where enum_name='billing_rules' and enum_id=200;
Update r_enum_value set enum_message_property='No Prorata' where enum_name='billing_rules' and enum_id=300;
Update r_enum_value set enum_message_property='Custom Prorata' where enum_name='billing_rules' and enum_id=400;

Drop procedure IF EXISTS addbillmaster; 
DELIMITER //
create procedure addbillmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'group_id'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_bill_master add group_id bigint(20) default null;

END IF;
  
END //
DELIMITER ;
call addbillmaster();

Drop procedure IF EXISTS addbillmaster;


Drop procedure IF EXISTS addticketmaster; 
DELIMITER //
create procedure addticketmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'source_of_ticket'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master add column source_of_ticket varchar(50) default NULL; 

END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'due_date'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master add column due_date datetime default NULL;

END IF;
  
END //
DELIMITER ;
call addticketmaster();

Drop procedure IF EXISTS addticketmaster;








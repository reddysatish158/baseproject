
DELIMITER $$
Drop procedure IF EXISTS addreorderlevel $$
create procedure addreorderlevel() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'reorder_level'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_item_master add `reorder_level` int(2) DEFAULT NULL;
end if;
END $$
delimiter ;
call addreorderlevel();
Drop procedure IF EXISTS addreorderlevel;

INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('logistics','UPDATE_GRN','GRN','UPDATE',1);



DELIMITER $$
Drop procedure IF EXISTS addpono $$
create procedure addpono() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'po_no'
     and TABLE_NAME = 'b_grn'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_grn add `po_no` varchar(20) DEFAULT NULL;
end if;
END $$
delimiter ;
call addpono();
Drop procedure IF EXISTS addpono ;


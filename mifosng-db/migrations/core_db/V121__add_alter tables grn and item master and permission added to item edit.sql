Alter table b_item_master add `reorder_level` int(2) DEFAULT NULL;

ALTER table b_grn add `po_no` varchar(20) DEFAULT NULL;

INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('logistics','UPDATE_GRN','GRN','UPDATE',1);

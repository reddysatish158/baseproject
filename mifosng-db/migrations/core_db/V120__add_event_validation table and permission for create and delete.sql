CREATE TABLE IF NOT EXISTS `b_event_validation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_name` varchar(60) DEFAULT NULL,
  `process` varchar(100) DEFAULT NULL,
  `pre_post` char(1) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;


INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('workflow','CREATE_EVENTVALIDATION','EVENTVALIDATION','CREATE',1);

INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('workflow','DELETE_EVENTVALIDATION','EVENTVALIDATION','DELETE',1);

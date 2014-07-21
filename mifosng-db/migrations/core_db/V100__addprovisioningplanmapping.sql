/*
New table for storing plan mapping details
*/

DROP TABLE IF EXISTS `b_prov_plan_details`;
CREATE TABLE `b_prov_plan_details` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `plan_id` int(10) NOT NULL,
  `plan_identification` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'n',
  PRIMARY KEY (`id`),
  UNIQUE KEY `plan_id` (`plan_id`,`plan_identification`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1 COMMENT='latin1_swedish_ci'

CREATE TABLE IF NOT EXISTS `b_item_audit` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `itemmaster_id` int(10) NOT NULL,
  `item_code` varchar(10) NOT NULL,
  `unit_price` decimal(22,6) NOT NULL,
  `changed_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

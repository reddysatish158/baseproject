CREATE TABLE IF NOT EXISTS `b_login_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(200) DEFAULT NULL,
  `device_id` varchar(200) DEFAULT NULL,
  `username` varchar(100) NOT NULL,   
  `session_id` varchar(200) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `logout_time` datetime DEFAULT NULL,
  `status` char(10) NOT NULL DEFAULT 'INACTIVE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=396 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';

insert IGNORE into m_permission values(null, 'portfolio', 'UPDATE_CLIENTBILLMODE', 'CLIENTBILLMODE', 'UPDATE', '0');

	alter table b_process_request_detail  
drop column service_type,
add column service_type varchar (20) DEFAULT NULL;

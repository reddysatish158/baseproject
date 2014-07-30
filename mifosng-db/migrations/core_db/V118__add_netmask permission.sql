INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('organization','UPDATE_IPDESCRIPTION','IPDESCRIPTION','UPDATE',1);

INSERT IGNORE INTO `b_eventaction_mapping`(event_name,action_name,process,is_deleted) 
VALUES ('Create Ticket','Send Email','workflow_events','N');

INSERT IGNORE INTO `b_eventaction_mapping`(event_name,action_name,process,is_deleted) 
VALUES ('Add Comment','Send Email','workflow_events','N');

INSERT IGNORE INTO `b_eventaction_mapping`(event_name,action_name,process,is_deleted) 
VALUES ('Close Ticket','Send Email','workflow_events','N');

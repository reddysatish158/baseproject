
package org.mifosplatform.billing.action.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_event_actions")
public class EventAction extends AbstractPersistable<Long> {
	
	@Column(name = "trans_date", nullable = false, length = 100)
	private Date transDate;

	@Column(name = "event_action", nullable = false, length = 20)
	private String eventAction;

	@Column(name = "entity_name", nullable = false, length = 100)
	private String entityName;

	@Column(name = "action_name", nullable = false, length = 100)
	private String actionName;

	@Column(name = "api_url", nullable = false, length = 100)
	private String apiUrl;
	
	@Column(name = "resource_id", nullable = false, length = 100)
	private Long resourceId;
	
	@Column(name = "command_as_json", nullable = false, length = 100)
	private String commandAsJson;
	
	@Column(name = "is_processed")
	private char isProcessed='N';


public EventAction(){}



public EventAction(Date transDate, String eventAction, String entityName,String actionName, String apiUrl, Long resourceId, String commandAsJson) {
	this.transDate=transDate;
	this.eventAction=eventAction;
	this.entityName=entityName;
	this.actionName=actionName;
	this.apiUrl=apiUrl;
	this.resourceId=resourceId;
	this.commandAsJson=commandAsJson;
	
	
}

}




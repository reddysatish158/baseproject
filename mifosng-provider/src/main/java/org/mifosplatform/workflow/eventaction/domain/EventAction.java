
package org.mifosplatform.workflow.eventaction.domain;

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
	
	@Column(name = "order_id", nullable = false, length = 100)
	private Long orderId;
	
	@Column(name = "client_id", nullable = false, length = 100)
	private Long clientId;
	
	@Column(name = "command_as_json", nullable = false, length = 100)
	private String commandAsJson;
	
	@Column(name = "is_processed")
	private char isProcessed='N';


public EventAction(){}



public EventAction(Date transDate, String eventAction, String entityName,String actionName, String apiUrl, Long resourceId,
		String commandAsJson, Long orderId, Long clientId) {
	this.transDate=transDate;
	this.eventAction=eventAction;
	this.entityName=entityName;
	this.actionName=actionName;
	this.apiUrl=apiUrl;
	this.resourceId=resourceId;
	this.commandAsJson=commandAsJson;
	this.orderId=orderId;
	this.clientId=clientId;
	
	
}



public void updateStatus(char status) {

	this.isProcessed=status;
	
}



public Date getTransDate() {
	return transDate;
}



public String getEventAction() {
	return eventAction;
}



public String getEntityName() {
	return entityName;
}



public String getActionName() {
	return actionName;
}



public String getApiUrl() {
	return apiUrl;
}



public Long getResourceId() {
	return resourceId;
}



public Long getOrderId() {
	return orderId;
}



public Long getClientId() {
	return clientId;
}



public String getCommandAsJson() {
	return commandAsJson;
}



public char IsProcessed() {
	return isProcessed;
}

}




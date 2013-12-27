package org.mifosplatform.billing.scheduledjobs.data;

public class EventActionData {
 
	private final Long id;
	private final String eventAction;
	private final String entityName;
	private final String actionName;
	private final String jsonData;
	private final Long resourceId;
	
	
	public EventActionData(Long id, String eventaction, String entityName,
			String actionName, String jsonData, Long resourceId) {
		
		this.id=id;
		this.eventAction=eventaction;
		this.entityName=entityName;
		this.actionName=actionName;
		this.jsonData=jsonData;
		this.resourceId=resourceId;
	
	}


	public Long getId() {
		return id;
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


	public Long getResourceId() {
		return resourceId;
	}


	public String getJsonData() {
		return jsonData;
	}
	
	

}

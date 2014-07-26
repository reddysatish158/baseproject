package org.mifosplatform.workflow.eventaction.data;

public class ActionDetaislData {
	
	private final Long id;
	private final String procedureName;
	private final String actionName;
	private final String isSynchronous;
	private final String eventName;

	public ActionDetaislData(Long id, String procedureName, String actionName, String isSynchronous,String eventName) {
            
		this.id=id;
		this.procedureName=procedureName;
		this.actionName=actionName;
		this.isSynchronous=isSynchronous;
		this.eventName=eventName;

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
		return procedureName;
	}

	/**
	 * @return the actionType
	 */
	public String getActionName() {
		return actionName;
	}


	public String IsSynchronous() {
		return isSynchronous;
	}

	public String getEventName() {
		return eventName;
	}
	
	

}

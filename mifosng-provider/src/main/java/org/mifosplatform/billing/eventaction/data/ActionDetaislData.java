package org.mifosplatform.billing.eventaction.data;

public class ActionDetaislData {
	
	private final Long id;
	private final String procedureName;
	private final String actionName;
	private final String isSynchronous;

	public ActionDetaislData(Long id, String procedureName, String actionName, String isSynchronous) {
            
		this.id=id;
		this.procedureName=procedureName;
		this.actionName=actionName;
		this.isSynchronous=isSynchronous;

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
	public String getaActionName() {
		return actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public String IsSynchronous() {
		return isSynchronous;
	}
	
	

}

package org.mifosplatform.billing.action.data;

public class ActionDetaislData {
	
	private final Long id;
	private final String procedureName;
	private final String actionName;

	public ActionDetaislData(Long id, String procedureName, String actionName) {
            
		this.id=id;
		this.procedureName=procedureName;
		this.actionName=actionName;

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
	
	

}

package org.mifosplatform.billing.action.data;

public class EventActionProcedureData {
	
	
	
	private final Long orderId;
	private final boolean isCheck;
	private final String actionName;
	private final String planId;
	

	public EventActionProcedureData(boolean isCheck, Long orderId, String actionName, String planId) {

            this.isCheck=isCheck;
            this.orderId=orderId;
            this.actionName=actionName;
            this.planId=planId;
	}


	public Long getOrderId() {
		return orderId;
	}


	public boolean isCheck() {
		return isCheck;
	}


	public String getActionName() {
		return actionName;
	}


	public String getPlanId() {
		return planId;
	}
	
	

}

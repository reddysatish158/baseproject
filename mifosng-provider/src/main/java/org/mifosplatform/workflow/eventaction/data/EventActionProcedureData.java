package org.mifosplatform.workflow.eventaction.data;

public class EventActionProcedureData {
	
	
	
	private final Long orderId;
	private final boolean isCheck;
	private final String actionName;
	private final String planId;
	private final String emailId;
	private final Long contractId;
	

	public EventActionProcedureData(boolean isCheck, Long orderId, String actionName, String planId,String emailId, Long contractId) {

            this.isCheck=isCheck;
            this.orderId=orderId;
            this.actionName=actionName;
            this.planId=planId;
            this.emailId=emailId;
            this.contractId=contractId;
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


	public String getEmailId() {
		return emailId;
	}


	public Long getContractId() {
		return contractId;
	}
	
	

}

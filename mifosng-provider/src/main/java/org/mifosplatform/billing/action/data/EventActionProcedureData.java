package org.mifosplatform.billing.action.data;

public class EventActionProcedureData {
	
	
	
	private final Long orderId;
	private final boolean isCheck;
	

	public EventActionProcedureData(boolean isCheck, Long orderId) {

            this.isCheck=isCheck;
            this.orderId=orderId;
	}


	public Long getOrderId() {
		return orderId;
	}


	public boolean isCheck() {
		return isCheck;
	}
	
	

}

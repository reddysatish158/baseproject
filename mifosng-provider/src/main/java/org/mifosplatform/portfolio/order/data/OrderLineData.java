package org.mifosplatform.portfolio.order.data;

public class OrderLineData {
	
	private final Long id;
	private final Long orderId;
	private final String servicecode;
	private final String serviceDescription;
	private final String serviceType;
	

	public OrderLineData(Long id, Long orderId, String serviceCode,String serviceDescription,
			String serviceType) {
		
		this.id=id;
		this.orderId=orderId;
		this.servicecode=serviceCode;
		this.serviceDescription=serviceDescription;
		this.serviceType=serviceType;
	}


	public Long getId() {
		return id;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getServicecode() {
		return servicecode;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}


	public String getServiceType() {
		return serviceType;
	}

	
}

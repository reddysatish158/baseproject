package org.mifosplatform.billing.entitlements.data;

public class ClientEntitlementData {

	private String emailId;
	private String fullName;

	public ClientEntitlementData(String emailId, String fullName) {
		// TODO Auto-generated constructor stub
		this.emailId=emailId;
		this.fullName=fullName;
		
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFullName() {
		return fullName;
	}
	
	

	
	
}

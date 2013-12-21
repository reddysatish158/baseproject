package org.mifosplatform.billing.entitlements.data;

public class ClientEntitlementData {

	private String emailId;
	private String fullName;
	private String status;
	private boolean results;

	public ClientEntitlementData(String emailId, String fullName) {
		// TODO Auto-generated constructor stub
		this.emailId=emailId;
		this.fullName=fullName;
		
	}

	public ClientEntitlementData(String status, boolean results) {	
		// TODO Auto-generated constructor stub
		this.status=status;
		this.results=results;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isResults() {
		return results;
	}

	public String getStatus() {
		return status;
	}

}

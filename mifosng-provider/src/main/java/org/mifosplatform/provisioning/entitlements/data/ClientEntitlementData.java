package org.mifosplatform.provisioning.entitlements.data;

public class ClientEntitlementData {

	private String emailId;
	private String fullName;
	private String status;
	private String login;
	private String password;
	private boolean results;

	public ClientEntitlementData(String emailId, String fullName,String login,String password) {
		// TODO Auto-generated constructor stub
		this.emailId=emailId;
		this.fullName=fullName;
		this.login=login;
		this.password=password;
		
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

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
	
	

}

package org.mifosplatform.billing.entitlements.data;

public class StakerData {

	
	private String mac;
	private Long Ls;
	private String status;
	private String fname;
	private String phone;
	private String end_date;
	private String tariff;
	private String base64EncodedAuthenticationKey;
	private Long results;
	private String error;
	
	
	public StakerData(String mac, Long ls, String status, String fname,
			String phone, String end_date, String tariff) {
		// TODO Auto-generated constructor stub
		this.mac=mac;
		this.Ls=ls;
		this.status=status;
		this.fname=fname;
		this.phone=phone;
		this.end_date=end_date;
		this.tariff=tariff;
	}


	public StakerData() {
		// TODO Auto-generated constructor stub
		
	}


	public String getMac() {
		return mac;
	}


	public Long getLs() {
		return Ls;
	}


	public String getStatus() {
		return status;
	}


	public String getFname() {
		return fname;
	}


	public String getPhone() {
		return phone;
	}


	public String getEnd_date() {
		return end_date;
	}


	public String getTariff() {
		return tariff;
	}


	public String getBase64EncodedAuthenticationKey() {
		return base64EncodedAuthenticationKey;
	}


	public void setBase64EncodedAuthenticationKey(
			String base64EncodedAuthenticationKey) {
		this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
	}


	public Long getResults() {
		return results;
	}


	public String getError() {
		return error;
	}


	public void setStatus(String status) {
		// TODO Auto-generated method stub
		this.status=status;
		
	}


	public void setError(String error) {
		// TODO Auto-generated method stub
		this.error=error;
		
	}


	public void setResults(Long value) {
		// TODO Auto-generated method stub
		this.results=value;
		
	}
	
	
	
	
	
	
}

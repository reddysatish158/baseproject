package org.mifosplatform.billing.entitlements.data;

public class StakerData {

	
	private String mac;
	private Long Ls;
	private String status;
	private String fname;
	private String phone;
	private String end_date;
	private String tariff;
	
	
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
	
	
	
	
}

package org.mifosplatform.organisation.ippool.data;

import java.util.List;

public class IpPoolManagementData {
	
	private Long id;
	private String ipAddress;
	private String ipPoolDescription;
	private String status;
	private Long clientId;
	private String clientName;
	private String notes;
	List<String> ipAddressData;
	
	public IpPoolManagementData(Long id, String ipAddress, String poolName,
			String status, Long clientId, String clientName, String notes) {
		
		this.id=id;
		this.ipAddress=ipAddress;
		this.ipPoolDescription=poolName;
		this.status=status;
		this.clientId=clientId;
		this.clientName=clientName;
		this.notes=notes;
		
	}

	public IpPoolManagementData(List<String> ipAddressData) {
		this.ipAddressData=ipAddressData;
	}

	public Long getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getIpPoolDescription() {
		return ipPoolDescription;
	}

	public String getStatus() {
		return status;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public String getNotes() {
		return notes;
	}

	public List<String> getIpAddressData() {
		return ipAddressData;
	}
	
}

package org.mifosplatform.organisation.ippool.data;

import java.util.List;

public class IpPoolManagementData {
	
	private Long id;
	private String ipAddress;
	private String ipPoolDescription;
	private String status;
	private Long clientId;
	List<String> ipAddressData;
	private String clientName;
	private String notes;
	
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
		// TODO Auto-generated constructor stub
		this.ipAddressData=ipAddressData;
	}
	
	
	

}

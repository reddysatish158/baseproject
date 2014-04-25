package org.mifosplatform.organisation.ippool.data;

public class IpPoolManagementData {
	
	private Long id;
	private String ipAddress;
	private String ipPoolDescription;
	private String status;
	private Long clientId;
	
	public IpPoolManagementData(Long id, String ipAddress, String poolName,
			String status, Long clientId) {
		
		this.id=id;
		this.ipAddress=ipAddress;
		this.ipPoolDescription=poolName;
		this.status=status;
		this.clientId=clientId;
	}
	
	
	

}

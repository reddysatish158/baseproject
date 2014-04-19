package org.mifosplatform.organisation.ippool.data;

public class IpPoolData {
	
	private final Long id;
	private final String poolName;
	private final String ipAddress;

	public IpPoolData(Long id, String poolName, String ipaddress) {

		this.id=id;
		this.poolName=poolName;
		this.ipAddress=ipaddress;
		
	}

}

package org.mifosplatform.billing.ippool.data;

public class IpPoolData {
	
	private final Long id;
	private final Long poolId;
	private final String ipAddress;

	public IpPoolData(Long id, Long poolId, String ipaddress) {

		this.id=id;
		this.poolId=poolId;
		this.ipAddress=ipaddress;
		
	}

}

package org.mifosplatform.organisation.ippool.data;

import java.util.Collection;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class IpPoolData {
	
	private  Long id;
	private  String poolName;
	private  String ipAddress;
	private Collection<MCodeData> codeValueDatas;

	public IpPoolData(Long id, String poolName, String ipaddress) {

		this.id=id;
		this.poolName=poolName;
		this.ipAddress=ipaddress;
		
	}

	public IpPoolData(Collection<MCodeData> codeValueDatas) {
		this.codeValueDatas=codeValueDatas;
	}

/*=======
	private final Long poolId;
	private final String ipAddress;
	

	public IpPoolData(Long id, Long poolId, String ipaddress) {
		
		this.id=id;
		this.poolId=poolId;
		this.ipAddress=ipaddress;
		
	}
	public Long getId() {
		return id;
	}
	public Long getPoolId() {
		return poolId;
	}
	public String getIpAddress() {
		return ipAddress;
	}
>>>>>>> obsplatform-1.01*/
}

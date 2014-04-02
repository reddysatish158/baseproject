package org.mifosplatform.billing.ippool.service;

import java.util.List;

import org.mifosplatform.billing.ippool.data.IpPoolData;

public interface IpPoolManagementReadPlatformService {

	List<IpPoolData> getUnallocatedIpAddressDetailds();

}

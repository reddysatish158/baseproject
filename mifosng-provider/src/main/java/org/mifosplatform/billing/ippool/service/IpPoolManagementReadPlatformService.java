package org.mifosplatform.billing.ippool.service;

import java.util.List;

import org.mifosplatform.billing.ippool.data.IpPoolData;
import org.mifosplatform.billing.paymode.data.McodeData;

public interface IpPoolManagementReadPlatformService {

	List<IpPoolData> getUnallocatedIpAddressDetailds();

}

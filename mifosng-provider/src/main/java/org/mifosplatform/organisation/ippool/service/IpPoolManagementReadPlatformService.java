package org.mifosplatform.organisation.ippool.service;

import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.organisation.ippool.data.IpPoolData;

public interface IpPoolManagementReadPlatformService {

	List<IpPoolData> getUnallocatedIpAddressDetailds();

}

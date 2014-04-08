package org.mifosplatform.billing.ippool.service;

import java.util.List;

import org.mifosplatform.billing.ippool.data.IpPoolData;
import org.mifosplatform.billing.ippool.data.IpPoolManagementData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface IpPoolManagementReadPlatformService {
	

	List<IpPoolData> getUnallocatedIpAddressDetailds();
	Long checkIpAddress(String ipaddress);

	List<IpPoolManagementData> retrieveAllData();

	Page<IpPoolManagementData> retrieveIpPoolData(SearchSqlQuery searchItemDetails, String type);
	
	


}

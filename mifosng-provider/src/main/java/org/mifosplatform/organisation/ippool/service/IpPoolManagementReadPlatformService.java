package org.mifosplatform.organisation.ippool.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;

public interface IpPoolManagementReadPlatformService {

	List<IpPoolData> getUnallocatedIpAddressDetailds();

	Long checkIpAddress(String ipaddress);

	Page<IpPoolManagementData> retrieveIpPoolData(
			SearchSqlQuery searchItemDetails, String type, String[] data);

	List<String> retrieveIpPoolIDArray(String query);

	IpPoolManagementData retrieveIpaddressData(String ipAddress);

	List<IpPoolManagementData> retrieveClientIpPoolDetails(Long clientId);

}

package org.mifosplatform.billing.ippool.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.ippool.data.IpPoolManagementData;
import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface IpPoolManagementReadPlatformService {

	Long checkIpAddress(String ipaddress);

	List<IpPoolManagementData> retrieveAllData();

	Page<IpPoolManagementData> retrieveIpPoolData(SearchSqlQuery searchItemDetails, String type);
	
	
	

}

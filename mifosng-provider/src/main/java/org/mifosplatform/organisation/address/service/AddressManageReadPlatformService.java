package org.mifosplatform.organisation.address.service;



import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.address.data.AddressManageData;



public interface AddressManageReadPlatformService {
	
	public Page<AddressManageData> retrieveAllAddresses(SearchSqlQuery searchAddresses);

}

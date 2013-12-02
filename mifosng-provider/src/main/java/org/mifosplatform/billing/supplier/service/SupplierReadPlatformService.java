package org.mifosplatform.billing.supplier.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.supplier.data.SupplierData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface SupplierReadPlatformService {
	
	public List<SupplierData> retrieveSupplier();

	public Page<SupplierData> retrieveSupplier(SearchSqlQuery searchSupplier);

}

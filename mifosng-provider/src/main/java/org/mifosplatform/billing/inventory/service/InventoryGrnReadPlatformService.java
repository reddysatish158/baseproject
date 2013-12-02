package org.mifosplatform.billing.inventory.service;

import java.util.Collection;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.inventory.data.InventoryGrnData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface InventoryGrnReadPlatformService {

	public Collection<InventoryGrnData> retriveGrnDetails();
	//public InventoryGrnData retriveGrnDetailTemplate();
	InventoryGrnData retriveGrnDetailTemplate(Long grnId);
	
	public boolean validateForExist(final Long grnId);
	public Collection<InventoryGrnData> retriveGrnIds();
	public Page<InventoryGrnData> retriveGrnDetails(SearchSqlQuery searchGrn);
	
}

package org.mifosplatform.logistics.itemdetails.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;

public interface InventoryGrnReadPlatformService {

	public Collection<InventoryGrnData> retriveGrnDetails();
	//public InventoryGrnData retriveGrnDetailTemplate();
	InventoryGrnData retriveGrnDetailTemplate(Long grnId);
	
	public boolean validateForExist(final Long grnId);
	public Collection<InventoryGrnData> retriveGrnIds();
	public Page<InventoryGrnData> retriveGrnDetails(SearchSqlQuery searchGrn);
	public List<McodeData> retrieveItemQualityData(String str);
	
}

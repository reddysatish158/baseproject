package org.mifosplatform.logistics.mrn.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;

public interface MRNDetailsReadPlatformService {

	List<MRNDetailsData> retriveMRNDetails();

	List<MRNDetailsData> retriveMrnDetailsTemplate();

	List<MRNDetailsData> retriveItemMasterDetails();

	Collection<MRNDetailsData> retriveMrnIds();
	
	List<Long> retriveItemMasterId(Long mrnId);

	List<Long> retriveItemDetailsId(String serialNumber, Long itemMasterId);

	MRNDetailsData retriveFromAndToOffice(Long mrnId);

	List<String> retriveSerialNumbers(Long fromOffice, Long toOffice);

	Page<InventoryTransactionHistoryData> retriveHistory(SearchSqlQuery searchItemHistory);
	
	 MRNDetailsData retriveSingleMrnDetail(Long mrnId);

	 Page<MRNDetailsData> retriveMRNDetails(SearchSqlQuery searchMRNDetails);

	InventoryTransactionHistoryData retriveSingleMovedMrn(Long mrnId);

	MRNDetailsData retriveAgentId(Long itemsaleId);

	List<String> retriveSerialNumbersForItems(Long officeId, Long itemsaleId, String serialNumber);

	List<Long> retriveItemMasterIdForSale(Long mrnId);
}

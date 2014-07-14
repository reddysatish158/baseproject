package org.mifosplatform.logistics.itemdetails.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.itemdetails.data.AllocationHardwareData;
import org.mifosplatform.logistics.itemdetails.data.InventoryItemDetailsData;
import org.mifosplatform.logistics.itemdetails.data.InventoryItemSerialNumberData;
import org.mifosplatform.logistics.itemdetails.data.ItemMasterIdData;
import org.mifosplatform.logistics.itemdetails.data.QuantityData;

public interface InventoryItemDetailsReadPlatformService {

	
	/*public Collection<InventoryItemDetailsData> retriveAllItemDetails();*/
	
	public InventoryItemDetailsData retriveIndividualItemDetails();

	public List<String> retriveSerialNumbers(Long oneTimeSaleId);
	
	public QuantityData retriveQuantity(Long oneTimeSaleId);
	
	public ItemMasterIdData retriveItemMasterId(Long oneTimeSaleId);
	
	public List<Long> retriveSerialNumberForItemMasterId(String serialNumber);

	public InventoryItemSerialNumberData retriveAllocationData(List<String> itemSerialNumbers,QuantityData quantityData, ItemMasterIdData itemMasterIdData);
	
	public AllocationHardwareData retriveInventoryItemDetail(String serialNumber);

	List<String> retriveSerialNumbers();

	public Page<InventoryItemDetailsData> retriveAllItemDetails(SearchSqlQuery searchItemDetails);

	public InventoryItemDetailsData retriveSingleItemDetail(Long itemId);

	public List<String> retriveSerialNumbersOnKeyStroke(Long oneTimeSaleId,String query, Long officeId);

	public InventoryItemSerialNumberData retriveAllocationData(
			List<String> itemSerialNumbers);
}

package org.mifosplatform.logistics.onetimesale.service;

import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;

public interface OneTimeSaleReadPlatformService {

	List<ItemData> retrieveItemData();

	List<OneTimeSaleData> retrieveClientOneTimeSalesData(Long clientId);

	List<OneTimeSaleData> retrieveOnetimeSaleDate(Long clientId);

	OneTimeSaleData retrieveSingleOneTimeSaleDetails(Long saleId);

	List<AllocationDetailsData> retrieveAllocationDetails(Long orderId);

	AllocationDetailsData retrieveAllocationDetailsBySerialNo(String serialNo);


}

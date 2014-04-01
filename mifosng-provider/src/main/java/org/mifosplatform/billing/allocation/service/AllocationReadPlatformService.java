package org.mifosplatform.billing.allocation.service;

import java.util.List;

import org.mifosplatform.billing.onetimesale.data.AllocationDetailsData;

public interface AllocationReadPlatformService {

	AllocationDetailsData getTheHardwareItemDetails(Long clientId, String configProp);

	List<AllocationDetailsData> retrieveHardWareDetailsByItemCode(Long clientId, String itemCode, String AssociationType);

	List<String> retrieveHardWareDetails(Long clientId);

	AllocationDetailsData getDisconnectedHardwareItemDetails(Long orderId,Long clientId);

}


package org.mifosplatform.billing.hardwareplanmapping.service;

import java.util.List;

import org.mifosplatform.billing.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.billing.plan.data.PlanCodeData;
import org.mifosplatform.logistics.item.data.ItemData;

public interface HardwarePlanReadPlatformService {
	
	List<HardwarePlanData> retrievePlanData(String itemCode);

	List<ItemData> retrieveItems();

	List<PlanCodeData> retrievePlans();

	HardwarePlanData retrieveSinglePlanData(Long planId);

	List<HardwarePlanData> retrieveItems(String itemCode);

}

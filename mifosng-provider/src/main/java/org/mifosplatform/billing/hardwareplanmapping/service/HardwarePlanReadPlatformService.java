
package org.mifosplatform.billing.hardwareplanmapping.service;

import java.util.List;

import org.mifosplatform.billing.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.billing.plan.data.PlanCodeData;

public interface HardwarePlanReadPlatformService {
	
	List<HardwarePlanData> retrievePlanData(String itemCode);

	List<ItemData> retrieveItems();

	List<PlanCodeData> retrievePlans();

	HardwarePlanData retrieveSinglePlanData(Long planId);

	List<HardwarePlanData> retrieveItems(String itemCode);

}

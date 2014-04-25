
package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.portfolio.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public interface HardwarePlanReadPlatformService {
	
	List<HardwarePlanData> retrievePlanData(String itemCode);

	List<ItemData> retrieveItems();

	List<PlanCodeData> retrievePlans();

	HardwarePlanData retrieveSinglePlanData(Long planId);

	List<HardwarePlanData> retrieveItems(String itemCode);

}

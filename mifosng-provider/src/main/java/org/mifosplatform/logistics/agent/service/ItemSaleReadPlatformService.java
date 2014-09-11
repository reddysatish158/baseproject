package org.mifosplatform.logistics.agent.service;

import java.util.List;

import org.mifosplatform.logistics.agent.data.AgentItemSaleData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;

public interface ItemSaleReadPlatformService {

	List<AgentItemSaleData> retrieveAllData();

	AgentItemSaleData retrieveSingleItemSaleData(Long id);
	
	List<MRNDetailsData> retriveItemsaleIds();

}

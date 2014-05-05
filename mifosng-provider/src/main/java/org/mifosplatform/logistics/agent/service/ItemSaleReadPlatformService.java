package org.mifosplatform.logistics.agent.service;

import java.util.List;

import org.mifosplatform.logistics.agent.data.AgentItemSaleData;

public interface ItemSaleReadPlatformService {

	List<AgentItemSaleData> retrieveAllData();

	AgentItemSaleData retrieveSingleItemSaleData(Long id);

}

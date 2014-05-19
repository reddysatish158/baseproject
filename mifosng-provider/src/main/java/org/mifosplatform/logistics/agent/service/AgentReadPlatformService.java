package org.mifosplatform.logistics.agent.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.logistics.agent.data.AgentItemSaleData;
import org.mifosplatform.logistics.itemdetails.mrn.data.MRNDetailsData;

public interface AgentReadPlatformService {

	List<AgentItemSaleData> retrieveAllData();

	List<MRNDetailsData> retriveItemsaleIds();

}

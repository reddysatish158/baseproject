package org.mifosplatform.organisation.redemption.service;

import java.util.List;

import org.mifosplatform.portfolio.order.data.OrderData;

public interface RedemptionReadPlatformService {
	
	List<Long> retrieveOrdersData(Long clientId,Long planId);

}

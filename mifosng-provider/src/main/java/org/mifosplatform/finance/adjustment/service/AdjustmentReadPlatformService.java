package org.mifosplatform.finance.adjustment.service;

import java.util.List;

import org.mifosplatform.finance.adjustment.data.AdjustmentData;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;

public interface AdjustmentReadPlatformService {
	
	List<ClientBalanceData> retrieveAllAdjustments(Long id);

	List<AdjustmentData> retrieveAllAdjustmentsCodes();

	
}

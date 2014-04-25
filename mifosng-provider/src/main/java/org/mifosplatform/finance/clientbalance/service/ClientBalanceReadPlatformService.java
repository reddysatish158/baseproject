package org.mifosplatform.finance.clientbalance.service;


import java.util.List;

import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;


public interface ClientBalanceReadPlatformService {

	
		ClientBalanceData retrieveBalance(Long clientid);

		List<ClientBalanceData> retrieveAllClientBalances(Long clientId);

	
	}


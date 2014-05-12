package org.mifosplatform.portfolio.transactionhistory.service;

import java.util.Date;

public interface TransactionHistoryWritePlatformService {

	
	public boolean saveTransactionHistory(Long clientId, String transactionType, Date transactionDate, Object... history);
	
}

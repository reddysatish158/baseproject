package org.mifosplatform.billing.transactionhistory.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.transactionhistory.data.TransactionHistoryData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TransactionHistoryReadPlatformService {

	public List<TransactionHistoryData> retriveTransactionHistoryTemplate();
	public Page<TransactionHistoryData> retriveTransactionHistoryById(SearchSqlQuery searchTransactionHistory, Long clientId);
}

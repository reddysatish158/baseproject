package org.mifosplatform.portfolio.transactionhistory.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.transactionhistory.data.TransactionHistoryData;

public interface TransactionHistoryReadPlatformService {

	public List<TransactionHistoryData> retriveTransactionHistoryTemplate();
	public Page<TransactionHistoryData> retriveTransactionHistoryById(SearchSqlQuery searchTransactionHistory, Long clientId);
}

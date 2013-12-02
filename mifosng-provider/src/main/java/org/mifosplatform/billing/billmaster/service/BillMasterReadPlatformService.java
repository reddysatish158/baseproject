package org.mifosplatform.billing.billmaster.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.billing.billingorder.data.BillDetailsData;
import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface BillMasterReadPlatformService {

	List<FinancialTransactionsData> retrieveFinancialData(Long clientId);
	Page<FinancialTransactionsData> retrieveInvoiceFinancialData(SearchSqlQuery searchFinancialTransaction,Long clientId);
	BillDetailsData retrievebillDetails(Long clientId);
	List<FinancialTransactionsData> getFinancialTransactionData(Long id);
	List<FinancialTransactionsData> retrieveStatments(Long clientId);
	BigDecimal retrieveClientBalance(Long clientId);
	List<FinancialTransactionsData> retrieveSingleInvoiceData(Long invoiceId);
	List<BillDetailsData> retrievegetStatementDetails(Long billId);

}

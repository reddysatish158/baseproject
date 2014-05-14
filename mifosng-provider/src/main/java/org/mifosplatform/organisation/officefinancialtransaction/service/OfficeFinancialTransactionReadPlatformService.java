package org.mifosplatform.organisation.officefinancialtransaction.service;

import java.util.Collection;

import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;

public interface OfficeFinancialTransactionReadPlatformService {

	Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(Long officeId);
}

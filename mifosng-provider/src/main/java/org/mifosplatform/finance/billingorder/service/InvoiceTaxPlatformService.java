package org.mifosplatform.finance.billingorder.service;

import java.util.List;

import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.domain.BillingOrder;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.domain.InvoiceTax;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface InvoiceTaxPlatformService {

	List<InvoiceTax> createInvoiceTax(List<InvoiceTaxCommand> command);

}

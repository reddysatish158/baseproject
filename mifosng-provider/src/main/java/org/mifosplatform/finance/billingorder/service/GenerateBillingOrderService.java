package org.mifosplatform.finance.billingorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;

public interface GenerateBillingOrderService {

	public List<BillingOrderCommand> generatebillingOrder(List<BillingOrderData> products);

	public List<InvoiceTaxCommand> generateInvoiceTax(List<TaxMappingRateData> taxMappingRateDatas,BigDecimal price,Long clientId);

	public Invoice generateInvoice(List<BillingOrderCommand> billingOrderCommands);

	
}

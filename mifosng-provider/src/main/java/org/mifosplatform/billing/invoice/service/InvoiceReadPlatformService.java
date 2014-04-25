
package org.mifosplatform.billing.invoice.service;

import java.util.List;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.finance.creditdistribution.data.CreditDistributionData;


public interface InvoiceReadPlatformService {

	List<InvoiceData> retrieveInvoiceDetails(Long id);

	List<InvoiceData> retrieveDueAmountInvoiceDetails(Long clientId);


}

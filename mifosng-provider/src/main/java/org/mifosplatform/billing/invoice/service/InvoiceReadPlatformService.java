
package org.mifosplatform.billing.invoice.service;

import java.util.List;

import org.mifosplatform.billing.creditdistribution.data.CreditDistributionData;
import org.mifosplatform.billing.invoice.data.InvoiceData;


public interface InvoiceReadPlatformService {

	List<InvoiceData> retrieveInvoiceDetails(Long id);

	List<InvoiceData> retrieveDueAmountInvoiceDetails(Long clientId);


}

package org.mifosplatform.billing.creditdistribution.data;

import java.util.List;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.billing.payments.data.PaymentData;

public class CreditDistributionData {
	
	private final List<InvoiceData> invoiceDatas;
	private final List<PaymentData> paymentDatas;

	public CreditDistributionData(List<InvoiceData> invoiceDatas,List<PaymentData> paymentDatas) {
		
		this.invoiceDatas=invoiceDatas;
		this.paymentDatas=paymentDatas;
		
		
	}

}

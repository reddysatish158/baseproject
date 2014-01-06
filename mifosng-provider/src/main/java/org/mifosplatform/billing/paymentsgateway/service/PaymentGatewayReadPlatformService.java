package org.mifosplatform.billing.paymentsgateway.service;

import java.util.List;

import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;

public interface PaymentGatewayReadPlatformService {
	
	Long retrieveClientIdForProvisioning(String serialNum);

	List<PaymentGatewayData> retrievePaymentGatewayData();
	

}

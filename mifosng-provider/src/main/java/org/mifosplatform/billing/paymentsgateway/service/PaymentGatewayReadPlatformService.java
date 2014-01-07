package org.mifosplatform.billing.paymentsgateway.service;

import java.util.List;

import org.mifosplatform.billing.paymentsgateway.data.PaymentEnum;
import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;

public interface PaymentGatewayReadPlatformService {
	
	Long retrieveClientIdForProvisioning(String serialNum);

	List<PaymentGatewayData> retrievePaymentGatewayData();

	List<MediaEnumoptionData> retrieveTemplateData();

	PaymentGatewayData retrievePaymentGatewayIdData(Long id);
	
	
	

}

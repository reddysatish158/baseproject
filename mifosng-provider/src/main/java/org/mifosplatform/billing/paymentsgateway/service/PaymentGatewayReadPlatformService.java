package org.mifosplatform.billing.paymentsgateway.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface PaymentGatewayReadPlatformService {
	
	Long retrieveClientIdForProvisioning(String serialNum);

	Page<PaymentGatewayData> retrievePaymentGatewayData(SearchSqlQuery searchItemDetails, String type);

	List<MediaEnumoptionData> retrieveTemplateData();

	PaymentGatewayData retrievePaymentGatewayIdData(Long id);
	
	
	

}

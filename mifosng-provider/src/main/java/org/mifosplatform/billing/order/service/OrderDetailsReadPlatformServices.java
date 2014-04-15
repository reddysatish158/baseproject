package org.mifosplatform.billing.order.service;

import java.util.List;

import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.pricing.data.PriceData;

public interface OrderDetailsReadPlatformServices {
	
	List<ServiceData> retrieveAllServices(Long plan_code);

	List<PriceData> retrieveAllPrices(Long plan_code, String billingFreq,Long clientId);

	List<PriceData> retrieveDefaultPrices(Long planId, String billingFrequency,Long clientId);

	int checkForCustomValidations(Long clientId, String eventCreateOrder, String strjson);

}

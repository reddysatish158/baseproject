package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.billing.pricing.data.PriceData;
import org.mifosplatform.portfolio.order.data.CustomValidationData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public interface OrderDetailsReadPlatformServices {
	
	List<ServiceData> retrieveAllServices(Long plan_code);

	List<PriceData> retrieveAllPrices(Long plan_code, String billingFreq,Long clientId);

	List<PriceData> retrieveDefaultPrices(Long planId, String billingFrequency,Long clientId);

	CustomValidationData checkForCustomValidations(Long clientId, String eventCreateOrder, String strjson);

}

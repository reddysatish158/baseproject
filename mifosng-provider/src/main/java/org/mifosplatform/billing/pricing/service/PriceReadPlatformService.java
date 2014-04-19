package org.mifosplatform.billing.pricing.service;

import java.util.List;

import org.mifosplatform.billing.charge.data.ChargesData;
import org.mifosplatform.billing.pricing.data.PricingData;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public interface PriceReadPlatformService {

	 List<ChargesData> retrieveChargeCode();
     List<EnumOptionData> retrieveChargeVariantData();
	List<DiscountMasterData> retrieveDiscountDetails();
//	List<PlanData> retrievePlanDetails();
	List<SubscriptionData> retrievePaytermData();
	List<ServiceData> retrievePriceDetails(String planCode);
	List<ServiceData> retrievePrcingDetails(Long planId);
	List<ServiceData> retrieveServiceCodeDetails(Long planCode);
	PricingData retrieveSinglePriceDetails(String priceId);
}

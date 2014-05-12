package org.mifosplatform.billing.discountmaster.service;

import java.util.List;

import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;

public interface DiscountReadPlatformService {

	List<DiscountMasterData> retrieveAllDiscounts();

	DiscountMasterData retrieveDiscountDetails(Long discountId);

}

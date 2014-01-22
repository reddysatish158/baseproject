package org.mifosplatform.billing.promotioncodes.service;

import java.util.List;

import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.billing.media.data.MediaassetAttribute;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public interface PromotionCodeReadPlatformService {

	List<PromotionCodeData> retrieveAllEventMapping();

	PromotionCodeData retriveSingleRecord(Long id);
	
/*	List<McodeData> retrieveEventMapData(String str);
	
	PromotionCodeData retrieveEventActionDetail(Long id);

	List<PromotionCodeData> retrieveEvents(String event);*/

}

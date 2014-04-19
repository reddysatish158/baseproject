package org.mifosplatform.billing.promotioncodes.service;

import java.util.List;

import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;

public interface PromotionCodeReadPlatformService {

	List<PromotionCodeData> retrieveAllEventMapping();

	PromotionCodeData retriveSingleRecord(Long id);
	
/*	List<McodeData> retrieveEventMapData(String str);
	
	PromotionCodeData retrieveEventActionDetail(Long id);

	List<PromotionCodeData> retrieveEvents(String event);*/

}

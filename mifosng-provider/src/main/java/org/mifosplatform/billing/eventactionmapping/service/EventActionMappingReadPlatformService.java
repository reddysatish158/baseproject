package org.mifosplatform.billing.eventactionmapping.service;

import java.util.List;

import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.billing.media.data.MediaassetAttribute;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public interface EventActionMappingReadPlatformService {

	List<EventActionMappingData> retrieveAllEventMapping();
	
	List<McodeData> retrieveEventMapData(String str);
	
	EventActionMappingData retrieveEventActionDetail(Long id);

	List<EventActionMappingData> retrieveEvents(String event);

}

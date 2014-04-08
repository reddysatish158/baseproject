package org.mifosplatform.billing.eventactionmapping.service;

import java.util.List;

import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.mifosplatform.cms.media.data.MediaassetAttribute;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.logistics.item.data.ItemData;

public interface EventActionMappingReadPlatformService {

	List<EventActionMappingData> retrieveAllEventMapping();
	
	List<McodeData> retrieveEventMapData(String str);
	
	EventActionMappingData retrieveEventActionDetail(Long id);

	List<EventActionMappingData> retrieveEvents(String event);

}

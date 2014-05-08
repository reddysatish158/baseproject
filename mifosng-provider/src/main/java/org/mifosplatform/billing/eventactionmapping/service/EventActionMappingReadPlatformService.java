package org.mifosplatform.billing.eventactionmapping.service;

import java.util.List;

import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.paymode.data.McodeData;

public interface EventActionMappingReadPlatformService {

	List<EventActionMappingData> retrieveAllEventMapping();
	
	List<McodeData> retrieveEventMapData(String str);
	
	EventActionMappingData retrieveEventActionDetail(Long id);

	List<EventActionMappingData> retrieveEvents(String event);

}

package org.mifosplatform.workflow.eventactionmapping.service;

import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.workflow.eventactionmapping.data.EventActionMappingData;

public interface EventActionMappingReadPlatformService {

	List<EventActionMappingData> retrieveAllEventMapping();
	
	List<McodeData> retrieveEventMapData(String str);
	
	EventActionMappingData retrieveEventActionDetail(Long id);

	List<EventActionMappingData> retrieveEvents(String event);

}

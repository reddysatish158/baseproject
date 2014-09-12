package org.mifosplatform.workflow.eventvalidation.service;

import java.util.List;
import org.mifosplatform.workflow.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.workflow.eventvalidation.data.EventValidationData;

public interface EventValidationReadPlatformService {

	List<EventValidationData> retrieveAllEventValidation();
	
}

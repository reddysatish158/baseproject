package org.mifosplatform.billing.action.service;

import java.util.List;

import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.data.EventActionProcedureData;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;

public interface ActionDetailsReadPlatformService {
	
	List<ActionDetaislData> retrieveActionDetails(String eventType);
	
	EventActionProcedureData checkCustomeValidationForEvents(Long clientId,String eventName,String actionName,String resourceId);


	List<EventActionData> retrieveAllActionsForProccessing();
}

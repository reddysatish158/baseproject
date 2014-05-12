package org.mifosplatform.workflow.eventaction.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.portfolio.order.data.SchedulingOrderData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.data.EventActionProcedureData;

public interface ActionDetailsReadPlatformService {
	
	List<ActionDetaislData> retrieveActionDetails(String eventType);
	
	EventActionProcedureData checkCustomeValidationForEvents(Long clientId,String eventName,String actionName,String resourceId);


	List<EventActionData> retrieveAllActionsForProccessing();

	Collection<SchedulingOrderData> retrieveClientSchedulingOrders(Long clientId);
}

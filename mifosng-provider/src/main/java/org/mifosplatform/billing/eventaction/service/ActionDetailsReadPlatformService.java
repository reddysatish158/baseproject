package org.mifosplatform.billing.eventaction.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.eventaction.data.ActionDetaislData;
import org.mifosplatform.billing.eventaction.data.EventActionProcedureData;
import org.mifosplatform.billing.order.data.SchedulingOrderData;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;

public interface ActionDetailsReadPlatformService {
	
	List<ActionDetaislData> retrieveActionDetails(String eventType);
	
	EventActionProcedureData checkCustomeValidationForEvents(Long clientId,String eventName,String actionName,String resourceId);


	List<EventActionData> retrieveAllActionsForProccessing();

	Collection<SchedulingOrderData> retrieveClientSchedulingOrders(Long clientId);
}

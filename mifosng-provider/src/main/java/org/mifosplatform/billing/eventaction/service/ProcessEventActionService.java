package org.mifosplatform.billing.eventaction.service;

import org.mifosplatform.billing.scheduledjobs.data.EventActionData;

public interface ProcessEventActionService {

	void ProcessEventActions(EventActionData eventActionData);

}

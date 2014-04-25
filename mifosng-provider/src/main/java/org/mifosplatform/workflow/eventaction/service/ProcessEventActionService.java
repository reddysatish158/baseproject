package org.mifosplatform.workflow.eventaction.service;

import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;

public interface ProcessEventActionService {

	void ProcessEventActions(EventActionData eventActionData);

}

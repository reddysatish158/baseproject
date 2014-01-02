package org.mifosplatform.billing.action.service;

import java.util.List;

import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;

public interface ActiondetailsWritePlatformService {

	void AddNewActions(List<ActionDetaislData> actionDetaislDatas, Long clientId, String resorceId);

	void ProcessEventActions(EventActionData eventActionData);

}

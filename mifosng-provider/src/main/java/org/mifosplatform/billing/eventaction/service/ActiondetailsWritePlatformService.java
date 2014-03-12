package org.mifosplatform.billing.eventaction.service;

import java.util.List;

import org.mifosplatform.billing.eventaction.data.ActionDetaislData;

public interface ActiondetailsWritePlatformService {

	void AddNewActions(List<ActionDetaislData> actionDetaislDatas, Long clientId, String resorceId);

	//void ProcessEventActions(EventActionData eventActionData);

}

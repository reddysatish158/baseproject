package org.mifosplatform.workflow.eventaction.service;

import java.util.List;

import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;

public interface ActiondetailsWritePlatformService {

	void AddNewActions(List<ActionDetaislData> actionDetaislDatas, Long clientId, String resorceId);

	//void ProcessEventActions(EventActionData eventActionData);

}

package org.mifosplatform.scheduledjobs.uploadstatus.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.scheduledjobs.uploadstatus.command.UploadStatusCommand;


public interface UploadStatusWritePlatformService {


	CommandProcessingResult addItem(UploadStatusCommand command);
//	CommandProcessingResult updateUploadStatus(Long orderId,ApiRequestJsonSerializationSettings settings);
	UploadStatusCommand convertJsonToUploadStatusCommand(Object object,String jsonRequestBody);
	CommandProcessingResult updateUploadStatus(Long orderId, int countno,
			ApiRequestJsonSerializationSettings settings);
	
	
	

}

package org.mifosplatform.cms.mediadevice.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface MediaDeviceWritePlatformService {

	CommandProcessingResult updateMediaDetailsStatus(JsonCommand command);

	CommandProcessingResult updateMediaDetailsCrashStatus(Long entityId,JsonCommand command);
	
	
}

package org.mifosplatform.provisioning.processrequest.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.processrequest.data.ProcessingDetailsData;

public interface ProcessRequestWriteplatformService {

	void ProcessingRequestDetails();

	void notifyProcessingDetails(ProcessingDetailsData detailsData);
	
	 CommandProcessingResult addProcessRequest(JsonCommand command);

}

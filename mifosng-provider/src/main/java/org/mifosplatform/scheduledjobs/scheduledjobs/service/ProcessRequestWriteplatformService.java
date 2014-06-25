package org.mifosplatform.scheduledjobs.scheduledjobs.service;

import org.mifosplatform.provisioning.processrequest.data.ProcessingDetailsData;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;

public interface ProcessRequestWriteplatformService {

	void ProcessingRequestDetails();

	void notifyProcessingDetails(ProcessRequest processRequest);

}

package org.mifosplatform.provisioning.processrequest.service;

import org.mifosplatform.provisioning.processrequest.data.ProcessingDetailsData;

public interface ProcessRequestWriteplatformService {

	void ProcessingRequestDetails();

	void notifyProcessingDetails(ProcessingDetailsData detailsData);

}

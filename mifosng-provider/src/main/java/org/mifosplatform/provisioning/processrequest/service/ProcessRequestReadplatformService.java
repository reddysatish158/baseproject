package org.mifosplatform.provisioning.processrequest.service;

import java.util.List;

import org.mifosplatform.provisioning.processrequest.data.ProcessingDetailsData;

public interface ProcessRequestReadplatformService {

	List<ProcessingDetailsData> retrieveProcessingDetails();

	List<ProcessingDetailsData> retrieveUnProcessingDetails();

	Long retrievelatestReqId(Long clientId, String oldHardware);

}

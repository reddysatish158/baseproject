package org.mifosplatform.provisioning.provisioning.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProvisioningWritePlatformService {

	CommandProcessingResult createProvisioning(JsonCommand command);

	CommandProcessingResult updateProvisioning(JsonCommand command);

	CommandProcessingResult deleteProvisioningSystem(JsonCommand command);

	CommandProcessingResult createNewProvisioningSystem(JsonCommand command,Long entityId);
	
	CommandProcessingResult updateProvisioningDetails(Long entityId);

	void updateHardwareDetails(Long clientId, String serialNumber,String oldSerialnumber, String provSerilaNum, String oldHardware);


}

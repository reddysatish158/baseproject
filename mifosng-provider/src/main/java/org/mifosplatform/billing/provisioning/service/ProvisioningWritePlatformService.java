package org.mifosplatform.billing.provisioning.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProvisioningWritePlatformService {

	CommandProcessingResult createProvisioning(JsonCommand command);

	CommandProcessingResult updateProvisioning(JsonCommand command);

	CommandProcessingResult deleteProvisioningSystem(JsonCommand command);

	CommandProcessingResult updateProvisioningDetails(Long entityId);

	void updateHardwareDetails(Long clientId, String serialNumber,String provSerailNum,String oldHardware);


}

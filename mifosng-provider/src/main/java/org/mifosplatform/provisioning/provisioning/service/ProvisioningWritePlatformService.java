package org.mifosplatform.provisioning.provisioning.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.domain.Order;

public interface ProvisioningWritePlatformService {

	CommandProcessingResult createProvisioning(JsonCommand command);

	CommandProcessingResult updateProvisioning(JsonCommand command);

	CommandProcessingResult deleteProvisioningSystem(JsonCommand command);

	CommandProcessingResult createNewProvisioningSystem(JsonCommand command,Long entityId);
	
	CommandProcessingResult updateProvisioningDetails(Long entityId);

	void updateHardwareDetails(Long clientId, String serialNumber,String oldSerialnumber, String provSerilaNum, String oldHardware);

	void postOrderDetailsForProvisioning(Order order, String planName, String reqType, Long prepareId,String groupName, String serialNo);
	


}

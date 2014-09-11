package org.mifosplatform.provisioning.provisioning.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ProvisioningServiceParamsWriteplatformService {

	CommandProcessingResult updateServiceParams(JsonCommand command,Long entityId);

}

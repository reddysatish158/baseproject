package org.mifosplatform.billing.selfcare.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SelfCareWritePlatformService {

	public CommandProcessingResult createSelfCare(JsonCommand command);

	/**
	 * @param command
	 * @return
	 */

	public CommandProcessingResult createSelfCareUDPassword(JsonCommand command);

	CommandProcessingResult updateClientStatus(JsonCommand command,Long entityId);
}

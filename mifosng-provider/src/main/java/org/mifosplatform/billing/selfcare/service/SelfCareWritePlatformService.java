package org.mifosplatform.billing.selfcare.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SelfCareWritePlatformService {

	public CommandProcessingResult createSelfCare(JsonCommand command);


	public CommandProcessingResult createSelfCareUDPassword(JsonCommand command);

	CommandProcessingResult updateClientStatus(JsonCommand command,Long entityId);

	CommandProcessingResult updateSelfCareUDPassword(JsonCommand command);

    CommandProcessingResult forgotSelfCareUDPassword(JsonCommand command);

	/*public void verifyActiveViewers(String serialNo, Long clientId);*/
   
}

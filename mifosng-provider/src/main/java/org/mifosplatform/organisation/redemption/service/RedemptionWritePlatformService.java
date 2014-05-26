package org.mifosplatform.organisation.redemption.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface RedemptionWritePlatformService {

	CommandProcessingResult createRedemption(JsonCommand command);
}

package org.mifosplatform.organisation.officepayments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface OfficePaymentsWritePlatformService {

	CommandProcessingResult createOfficePayment(JsonCommand command);
}

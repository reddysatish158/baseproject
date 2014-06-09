package org.mifosplatform.organisation.randomgenerator.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface RandomGeneratorWritePlatformService {

	CommandProcessingResult createRandomGenerator(JsonCommand command);

	CommandProcessingResult GenerateVoucherPinKeys(Long batchId);
	

	
}

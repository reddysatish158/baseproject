package org.mifosplatform.billing.creditdistribution.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface  CreditDistributionWritePlatformService {

	CommandProcessingResult createCreditDistribution(JsonCommand command);



}

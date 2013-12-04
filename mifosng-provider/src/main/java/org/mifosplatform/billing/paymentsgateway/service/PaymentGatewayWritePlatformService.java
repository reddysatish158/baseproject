package org.mifosplatform.billing.paymentsgateway.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PaymentGatewayWritePlatformService {

	CommandProcessingResult createPaymentGateway(JsonCommand command);
	

}

package org.mifosplatform.billing.paymentsgateway.handler;

import org.mifosplatform.billing.paymentsgateway.service.PaymentGatewayWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatePaymentGatewayCommandHandler implements NewCommandSourceHandler {

	 
	private final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService;
	
	@Autowired
	public CreatePaymentGatewayCommandHandler(final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService)
	{
	this.paymentGatewayWritePlatformService =paymentGatewayWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.paymentGatewayWritePlatformService.createPaymentGateway(command);
	}
}

package org.mifosplatform.finance.paymentsgateway.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentGatewayCommandHandler implements NewCommandSourceHandler {

	 
	private final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService;
	
	@Autowired
	public CreatePaymentGatewayCommandHandler(final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService)
	{
	this.paymentGatewayWritePlatformService =paymentGatewayWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.paymentGatewayWritePlatformService.createPaymentGateway(command);
	}
}

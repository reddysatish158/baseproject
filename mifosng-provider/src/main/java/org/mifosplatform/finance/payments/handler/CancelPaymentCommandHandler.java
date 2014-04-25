package org.mifosplatform.finance.payments.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.payments.service.PaymentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelPaymentCommandHandler implements NewCommandSourceHandler {

	private final PaymentWritePlatformService writePlatformService;

	@Autowired
	public CancelPaymentCommandHandler(final PaymentWritePlatformService writePlatformService) {
		
		this.writePlatformService = writePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.writePlatformService.cancelPayment(command,command.entityId());
	}
	
	
}

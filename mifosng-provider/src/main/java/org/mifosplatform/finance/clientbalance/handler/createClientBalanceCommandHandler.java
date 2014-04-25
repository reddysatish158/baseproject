package org.mifosplatform.finance.clientbalance.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class createClientBalanceCommandHandler implements NewCommandSourceHandler{

	
	private final ClientBalanceWritePlatformService balanceWritePlatformService;
	
	@Autowired
	public createClientBalanceCommandHandler(final ClientBalanceWritePlatformService balanceWritePlatformService) {
		this.balanceWritePlatformService = balanceWritePlatformService;
		
	}
	
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return balanceWritePlatformService.addClientBalance(command);
	}
}

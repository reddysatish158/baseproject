package org.mifosplatform.organisation.message.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.message.service.BillingMessageTemplateWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBillingMessageTemplateCommandHandler implements NewCommandSourceHandler {
	
private final BillingMessageTemplateWritePlatformService billingMessageTemplateWritePlatformService;
	
	@Autowired
	public CreateBillingMessageTemplateCommandHandler(
			final BillingMessageTemplateWritePlatformService billingMessageTemplateWritePlatformService)
	{
	this.billingMessageTemplateWritePlatformService =billingMessageTemplateWritePlatformService;
	}

	 @Transactional
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.billingMessageTemplateWritePlatformService.addMessageTemplate(command);
	}
	
}

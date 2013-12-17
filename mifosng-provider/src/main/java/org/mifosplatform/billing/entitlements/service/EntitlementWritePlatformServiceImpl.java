package org.mifosplatform.billing.entitlements.service;

import java.util.List;

import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EntitlementWritePlatformServiceImpl implements EntitlementWritePlatformService{
	
	private final ProcessRequestRepository entitlementRepository;
	private final PlatformSecurityContext context;
	
	
	@Autowired
	public EntitlementWritePlatformServiceImpl(final PlatformSecurityContext context
			,final ProcessRequestRepository entitlementRepository ) {		
		this.context = context;
		this.entitlementRepository=entitlementRepository;
	}
	
	
	
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		// TODO Auto-generated method stub
		//context.authenticatedUser();
		ProcessRequest request=this.entitlementRepository.findOne(command.entityId());
		String receiveMessage = command.stringValueOfParameterNamed("receiveMessage");
		if(receiveMessage.contains("failure :")){
		request.setProcessFailureStatus();
		}else{
			request.setProcessStatus();
		}
		List<ProcessRequestDetails> details=request.getProcessRequestDetails();
		
		for(ProcessRequestDetails processRequestDetails:details){
			processRequestDetails.updateStatus(command);
		}
		
		this.entitlementRepository.save(request);
		return new CommandProcessingResult(request.getId());
		
	}

	
	
}

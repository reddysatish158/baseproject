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
		char status;
		if(receiveMessage.contains("failure :")){
		         status='F';
		}else{
			status='Y';
		}
		
		List<ProcessRequestDetails> details=request.getProcessRequestDetails();
		
		for(ProcessRequestDetails processRequestDetails:details){			    			 			 	
			 	Long id = command.longValueOfParameterNamed("prdetailsId");
				if (processRequestDetails.getId().longValue() == id.longValue()) {
					processRequestDetails.updateStatus(command);	
				}
		}
		
		if(request.getRequestType().equalsIgnoreCase("DEVICE_SWAP") && !checkProcessDetailsUpdated(details)){
			status='F';
		}
		request.setProcessStatus(status);
		
		this.entitlementRepository.save(request);
		return new CommandProcessingResult(request.getId());
		
	}

	private boolean checkProcessDetailsUpdated(List<ProcessRequestDetails> details) {
		boolean flag=true;
		//for(ProcessRequestDetails processRequestDetails:details){
			if(details.get(0).getReceiveMessage().contains("failure : Exce")){
				flag=false;
			}
		//}
		return flag;
	}

	
	
}

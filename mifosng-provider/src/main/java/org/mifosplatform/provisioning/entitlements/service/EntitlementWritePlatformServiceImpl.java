package org.mifosplatform.provisioning.entitlements.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EntitlementWritePlatformServiceImpl implements EntitlementWritePlatformService{
	
	private final ProcessRequestRepository entitlementRepository;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	
	@Autowired
	public EntitlementWritePlatformServiceImpl(final ProcessRequestWriteplatformService processRequestWriteplatformService,
			                            final ProcessRequestRepository entitlementRepository ) {		

		this.processRequestWriteplatformService=processRequestWriteplatformService;
		this.entitlementRepository=entitlementRepository;
	}
	
	
	
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		// TODO Auto-generated method stub
		//context.authenticatedUser();
		ProcessRequest processRequest=this.entitlementRepository.findOne(command.entityId());
		String receiveMessage = command.stringValueOfParameterNamed("receiveMessage");
		char status;
		if(receiveMessage.contains("failure :")){
		         status='F';
		}else{
			status='Y';
		}
		
		List<ProcessRequestDetails> details=processRequest.getProcessRequestDetails();
		
		for(ProcessRequestDetails processRequestDetails:details){			    			 			 	
			 	Long id = command.longValueOfParameterNamed("prdetailsId");
				if (processRequestDetails.getId().longValue() == id.longValue()) {
					processRequestDetails.updateStatus(command);	
				}
		}
		
		if(processRequest.getRequestType().equalsIgnoreCase("DEVICE_SWAP") && !checkProcessDetailsUpdated(details)){
			status='F';
		}
		processRequest.setProcessStatus(status);
		
		//this.entitlementRepository.save(processRequest);
		this.processRequestWriteplatformService.notifyProcessingDetails(processRequest);
		return new CommandProcessingResult(processRequest.getId());
		
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

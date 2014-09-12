package org.mifosplatform.workflow.eventvalidation.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventvalidation.domain.EventValidation;
import org.mifosplatform.workflow.eventvalidation.domain.EventValidationRepository;
import org.mifosplatform.workflow.eventvalidation.exception.EventValidationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class EventValidationWritePlatformServiceImpl implements EventValidationWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(EventValidationWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
	private final EventValidationRepository eventValidationRepository;
	
@Autowired	
public EventValidationWritePlatformServiceImpl(final PlatformSecurityContext context,
		final EventValidationRepository eventValidationRepository)
{
	this.context=context;
	this.eventValidationRepository=eventValidationRepository;
}

	@Override
	public CommandProcessingResult createEventValidation(JsonCommand command) {
       
		try{
			
			this.context.authenticatedUser();
			/*this.apiJsonDeserializer.validateForCreate(command.json());*/
			EventValidation eventValidation=EventValidation.fromJson(command);
			
			this.eventValidationRepository.save(eventValidation);
			return new CommandProcessingResult(eventValidation.getId());
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
	
	}

	
	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

		
	@Override
	public CommandProcessingResult deleteEventValidation(Long id) {

     try{
    	 this.context.authenticatedUser();
    	 
    	 EventValidation event = this.eventValidationRepository.findOne(id);
    	 
    	 if(event==null){
    		 throw new EventValidationNotFoundException(id.toString());
    	 }
    	 
    	 event.delete();
    	 this.eventValidationRepository.save(event);
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(Exception exception){
    	 return null;
     }
	}
}


package org.mifosplatform.workflow.eventactionmapping.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.discountmaster.exceptions.DiscountNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.workflow.eventactionmapping.domain.EventActionMapping;
import org.mifosplatform.workflow.eventactionmapping.domain.EventActionMappingRepository;
import org.mifosplatform.workflow.eventactionmapping.exception.EventNameDuplicateException;
import org.mifosplatform.workflow.eventactionmapping.serialization.EventActionMappingCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class EventActionMappingWritePlatformServiceImpl implements EventActionMappingWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(EventActionMappingWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
	private final EventActionMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final EventActionMappingRepository eventActionMappingRepository;
	private final EventActionMappingReadPlatformService eventActionMappingReadPlatformService;
	
@Autowired	
public EventActionMappingWritePlatformServiceImpl(final PlatformSecurityContext context,EventActionMappingCommandFromApiJsonDeserializer apiJsonDeserializer,
		final EventActionMappingRepository eventActionMappingRepository,final EventActionMappingReadPlatformService eventActionMappingReadPlatformService)
{
	this.context=context;
	this.apiJsonDeserializer=apiJsonDeserializer;
	this.eventActionMappingRepository=eventActionMappingRepository;
	this.eventActionMappingReadPlatformService=eventActionMappingReadPlatformService;
}

	@Override
	public CommandProcessingResult createEventActionMapping(JsonCommand command) {
       
		try{
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			EventActionMapping eventActionMapping=EventActionMapping.fromJson(command);
			List<EventActionMappingData> datas=this.eventActionMappingReadPlatformService.retrieveEvents(eventActionMapping.getEventName());
            for(EventActionMappingData data:datas){
          	  
          	   if(data.getActionName().equalsIgnoreCase(eventActionMapping.getActionName())){
          		  throw new EventNameDuplicateException(eventActionMapping.getActionName());   
          	   }
          	  
            }
			this.eventActionMappingRepository.save(eventActionMapping);
			return new CommandProcessingResult(eventActionMapping.getId());
			
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
	
	}

	
	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("discountcode")) {
	            final String name = command.stringValueOfParameterNamed("discountcode");
	            throw new PlatformDataIntegrityException("error.msg.discount.duplicate.name", "A discount with Code'"
	                    + name + "'already exists");
	        }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

@Override
	public CommandProcessingResult updateEventActionMapping(Long id,JsonCommand command)
	{
        try{
        	  
        	this.context.authenticatedUser();
        	this.apiJsonDeserializer.validateForCreate(command.json());
        	
        	EventActionMapping eventAction=EventActionretrieveById(id);
        	final Map<String, Object> changes = eventAction.update(command);  
        	
        	if(!changes.isEmpty()){
        		this.eventActionMappingRepository.save(eventAction);
        	}
        	
        	return new CommandProcessingResult(id);
        	
        }catch(DataIntegrityViolationException dve){
        	handleCodeDataIntegrityIssues(command, dve);
        	return null;        }
		
         
}

	private EventActionMapping EventActionretrieveById(Long id) {
             
		EventActionMapping eventAction=this.eventActionMappingRepository.findOne(id);
              if (eventAction== null) { throw new DiscountNotFoundException(id.toString()); }
	          return eventAction;	
	}
	
	
	
	@Override
	public CommandProcessingResult deleteEventActionMapping(Long id) {

     try{
    	 this.context.authenticatedUser();
    	 EventActionMapping event=this.eventActionMappingRepository.findOne(id);
    	 
    	 if(event==null){
    		 throw new DiscountNotFoundException(id.toString());
    	 }
    	 
    	 event.delete();
    	 this.eventActionMappingRepository.save(event);
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(Exception exception){
    	 return null;
     }
	}
}

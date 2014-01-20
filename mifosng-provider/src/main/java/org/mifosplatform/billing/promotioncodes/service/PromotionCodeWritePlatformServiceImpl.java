/*

public class PromotionCodeWritePlatformServiceImpl {

}
*/

package org.mifosplatform.billing.promotioncodes.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.discountmaster.serialization.DiscountCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.eventactionmapping.domain.EventActionMapping;
import org.mifosplatform.billing.eventactionmapping.domain.EventActionMappingRepository;
import org.mifosplatform.billing.eventactionmapping.exception.EventActionMappingNotFoundException;
import org.mifosplatform.billing.eventactionmapping.exception.EventNameDuplicateException;
import org.mifosplatform.billing.eventactionmapping.serialization.EventActionMappingCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.mifosplatform.billing.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.billing.hardwareplanmapping.exception.ItemCodeDuplicateException;
import org.mifosplatform.billing.hardwareplanmapping.service.HardwarePlanReadPlatformService;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCode;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCodeRepository;
import org.mifosplatform.billing.promotioncodes.serialization.PromotionCodeCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.codes.exception.DiscountNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class PromotionCodeWritePlatformServiceImpl implements PromotionCodeWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(PromotionCodeWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
	private final PromotionCodeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final PromotionCodeRepository promotionMappingRepository;
	private final EventActionMappingReadPlatformService eventActionMappingReadPlatformService;
	
@Autowired	
public PromotionCodeWritePlatformServiceImpl(final PlatformSecurityContext context,PromotionCodeCommandFromApiJsonDeserializer apiJsonDeserializer,
		final PromotionCodeRepository promotionMappingRepository,final EventActionMappingReadPlatformService eventActionMappingReadPlatformService)
{
	this.context=context;
	this.apiJsonDeserializer=apiJsonDeserializer;
	this.promotionMappingRepository=promotionMappingRepository;
	this.eventActionMappingReadPlatformService=eventActionMappingReadPlatformService;
}

	@Override
	public CommandProcessingResult createPromotionCode(JsonCommand command) {
       
		try{
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PromotionCode promotioncode=PromotionCode.fromJson(command);
			
			this.promotionMappingRepository.save(promotioncode);
			return new CommandProcessingResult(promotioncode.getId());
			
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
	
	}

	
	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	       /* if (realCause.getMessage().contains("discountcode")) {
	            final String name = command.stringValueOfParameterNamed("discountcode");
	            throw new PlatformDataIntegrityException("error.msg.discount.duplicate.name", "A discount with Code'"
	                    + name + "'already exists", "displayName", name);
	        }*/

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

/*@Override
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
              if (eventAction== null) { throw new EventActionMappingNotFoundException(id.toString()); }
	          return eventAction;	
	}
	
	
	
	@Override
	public CommandProcessingResult deleteEventActionMapping(Long id) {

     try{
    	 this.context.authenticatedUser();
    	 EventActionMapping event=this.eventActionMappingRepository.findOne(id);
    	 
    	 if(event==null){
    		 throw new EventActionMappingNotFoundException(id.toString());
    	 }
    	 
    	 event.delete();
    	 this.eventActionMappingRepository.save(event);
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(Exception exception){
    	 return null;
     }
	}
*/}

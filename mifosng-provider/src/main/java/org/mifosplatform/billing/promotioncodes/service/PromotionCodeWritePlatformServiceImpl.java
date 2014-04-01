/*

public class PromotionCodeWritePlatformServiceImpl {

}
*/

package org.mifosplatform.billing.promotioncodes.service;

import java.util.Map;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCode;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCodeRepository;
import org.mifosplatform.billing.promotioncodes.exception.PromotionCodeNotFoundException;
import org.mifosplatform.billing.promotioncodes.serialization.PromotionCodeCommandFromApiJsonDeserializer;
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
	
@Autowired	
public PromotionCodeWritePlatformServiceImpl(final PlatformSecurityContext context,PromotionCodeCommandFromApiJsonDeserializer apiJsonDeserializer,
		final PromotionCodeRepository promotionMappingRepository)
{
	this.context=context;
	this.apiJsonDeserializer=apiJsonDeserializer;
	this.promotionMappingRepository=promotionMappingRepository;
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
	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

@Override
	public CommandProcessingResult updatePromotionCode(Long id,JsonCommand command)
	{
        try{
        	  
        	this.context.authenticatedUser();
        	//this.apiJsonDeserializer.validateForCreate(command.json());
        	
        	PromotionCode promotionCode=PromotionCoderetrieveById(id);
        	final Map<String, Object> changes = promotionCode.update(command);  
        	
        	if(!changes.isEmpty()){
        		this.promotionMappingRepository.save(promotionCode);
        	}
        	
        	return new CommandProcessingResult(id);
        	
        }catch(DataIntegrityViolationException dve){
        	handleCodeDataIntegrityIssues(command, dve);
        	return null;        }
		
         
}

	private PromotionCode PromotionCoderetrieveById(Long id) {
             
		PromotionCode promotionCode=this.promotionMappingRepository.findOne(id);
              if (promotionCode== null) { throw new PromotionCodeNotFoundException(id.toString()); }
	          return promotionCode;	
	}
	
	
	
	@Override
	public CommandProcessingResult deletePromotionCode(Long id) {

     try{
    	 this.context.authenticatedUser();
    	 PromotionCode promotionCode=this.promotionMappingRepository.findOne(id);
    	 
    	 if(promotionCode==null){
    		 throw new PromotionCodeNotFoundException(id.toString());
    	 }
    	 
    	 promotionCode.delete();
    	 this.promotionMappingRepository.save(promotionCode);
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(Exception exception){
    	 return null;
     }
	}
}

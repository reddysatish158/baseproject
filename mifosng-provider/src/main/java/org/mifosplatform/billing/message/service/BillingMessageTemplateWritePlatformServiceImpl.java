package org.mifosplatform.billing.message.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.mifosplatform.billing.message.data.BillingMessageData;
import org.mifosplatform.billing.message.domain.BillingMessageParam;
import org.mifosplatform.billing.message.domain.BillingMessageParamRepository;
import org.mifosplatform.billing.message.domain.BillingMessageTemplate;
import org.mifosplatform.billing.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.billing.message.serialization.BillingMessageTemplateCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.paymode.exception.PaymodeNotFoundException;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class BillingMessageTemplateWritePlatformServiceImpl implements BillingMessageTemplateWritePlatformService{

	    private final PlatformSecurityContext context;
	    private final FromJsonHelper fromApiJsonHelper;
	    private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	    private final BillingMessageParamRepository billingMessageParamRepository;
	    private final BillingMessageTemplateCommandFromApiJsonDeserializer billingMessageTemplateCommandFromApiJsonDeserializer;
	   
	    @Autowired
	    public BillingMessageTemplateWritePlatformServiceImpl(PlatformSecurityContext context,
	    	    FromJsonHelper fromApiJsonHelper,BillingMessageParamRepository billingMessageParamRepository,
	    		BillingMessageTemplateRepository billingMessageTemplateRepository, 
	    		BillingMessageTemplateCommandFromApiJsonDeserializer billingMessageTemplateCommandFromApiJsonDeserializer)
	    {
	    	this.context=context;
	    	this.fromApiJsonHelper=fromApiJsonHelper;
	    	this.billingMessageParamRepository=billingMessageParamRepository;
	    	this.billingMessageTemplateRepository=billingMessageTemplateRepository;
	    	this.billingMessageTemplateCommandFromApiJsonDeserializer=billingMessageTemplateCommandFromApiJsonDeserializer;
	    }
	
	    
	 //for post method
	    @Override
	    public CommandProcessingResult addMessageTemplate(JsonCommand command) {
			
			try {
				context.authenticatedUser();
				this.billingMessageTemplateCommandFromApiJsonDeserializer.validateForCreate(command.json());
				 BillingMessageTemplate  billingMessageTemplate = BillingMessageTemplate.fromJson(command);
								 
				 final JsonArray billingMessageparamArray=command.arrayOfParameterNamed("messageParams").getAsJsonArray();
				 Long number=0L;
				 if(billingMessageparamArray!=null){
				     for (JsonElement jsonelement : billingMessageparamArray) {	
				    	     final String parameterName = fromApiJsonHelper.extractStringNamed("parameter", jsonelement);   
				    	     number=number+1;
				    	     BillingMessageParam billingMessageParam = new BillingMessageParam(number, parameterName);
				    	     billingMessageTemplate.setBillingMessageParam(billingMessageParam);				 
				     }
				 }
				 this.billingMessageTemplateRepository.save(billingMessageTemplate);
	            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(billingMessageTemplate.getId()).build();
			    
			} catch (DataIntegrityViolationException dve) {
	            return CommandProcessingResult.empty();
	        }
			
		}

	    //for put method
		@Override
		public CommandProcessingResult updateMessageTemplate(JsonCommand command) {
			try {
				context.authenticatedUser();
				this.billingMessageTemplateCommandFromApiJsonDeserializer.validateForCreate(command.json());
				final BillingMessageTemplate messageTemplate=retriveMessageBy(command.entityId());
				final Map<String, Object> changes = messageTemplate.updateMessageTemplate(command);
				messageTemplate.getDetails().clear();
				
				final JsonArray billingMessageparamArray=command.arrayOfParameterNamed("messageParams").getAsJsonArray();
				 Long number=0L;
				 if(billingMessageparamArray!=null){
				     for (JsonElement jsonelement : billingMessageparamArray) {	
				    	     final String parameterName = fromApiJsonHelper.extractStringNamed("parameter", jsonelement);   
				    	     number=number+1;
				    	     BillingMessageParam billingMessageParam = new BillingMessageParam(number, parameterName);
				    	     messageTemplate.setBillingMessageParam(billingMessageParam);				 
				     }
				 }
				   
				     this.billingMessageTemplateRepository.save(messageTemplate);
				     
	                 return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(messageTemplate.getId()).build();
			  
			         } catch (DataIntegrityViolationException dve) {
	                            return CommandProcessingResult.empty();
	                    }
			
		       }
		
		@SuppressWarnings("null")
		@Override
		public CommandProcessingResult deleteMessageTemplate(JsonCommand command) {
			// TODO Auto-generated method stub
			context.authenticatedUser();
			final BillingMessageTemplate messageTemplate=retriveMessageBy(command.entityId());
			if (messageTemplate == null) {
				throw new DataIntegrityViolationException(messageTemplate.toString());
			}
			messageTemplate.isDelete();
		    this.billingMessageTemplateRepository.save(messageTemplate);
			return new CommandProcessingResultBuilder().withEntityId(command.entityId()).build();
		}
		
		
		private BillingMessageTemplate retriveMessageBy(Long MessageId) {
			final BillingMessageTemplate billingMessageTemplate = this.billingMessageTemplateRepository.findOne(MessageId);
			return billingMessageTemplate;
		}

}

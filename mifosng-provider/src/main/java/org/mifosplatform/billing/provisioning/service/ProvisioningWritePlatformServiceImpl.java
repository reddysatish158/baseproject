package org.mifosplatform.billing.provisioning.service;

import java.util.List;
import java.util.Map;

import org.hibernate.engine.profile.Association;
import org.mifosplatform.billing.association.domain.HardwareAssociation;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetails;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetailsRepository;
import org.mifosplatform.billing.inventory.exception.ActivePlansFoundException;
import org.mifosplatform.billing.order.domain.HardwareAssociationRepository;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.ownedhardware.data.OwnedHardware;
import org.mifosplatform.billing.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.billing.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {

	

	private final PlatformSecurityContext context;
	//private final JdbcTemplate jdbcTemplate;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ProvisioningCommandRepository provisioningCommandRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final ProvisioningReadPlatformService provisioningReadPlatformService;
    private final GlobalConfigurationRepository globalConfigurationRepository;
    private final OwnedHardwareJpaRepository hardwareJpaRepository;
    private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
    private final OrderReadPlatformService orderReadPlatformService;
    private final HardwareAssociationRepository associationRepository;
    private final ProcessRequestReadplatformService processRequestReadplatformService;
    
    @Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,
			final ProvisioningCommandRepository provisioningCommandRepository,final ProcessRequestRepository processRequestRepository,
			final OrderRepository orderRepository,final ProvisioningReadPlatformService provisioningReadPlatformService,
			final GlobalConfigurationRepository globalConfigurationRepository,final OwnedHardwareJpaRepository hardwareJpaRepository,
			final InventoryItemDetailsRepository inventoryItemDetailsRepository,OrderReadPlatformService orderReadPlatformService,
			final HardwareAssociationRepository associationRepository,final ProcessRequestReadplatformService processRequestReadplatformService) {
		
		this.context = context;		
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.provisioningCommandRepository=provisioningCommandRepository;
		this.processRequestRepository=processRequestRepository;
		this.provisioningReadPlatformService=provisioningReadPlatformService;
		this.globalConfigurationRepository=globalConfigurationRepository;
		this.hardwareJpaRepository=hardwareJpaRepository;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		this.orderReadPlatformService=orderReadPlatformService;
		this.associationRepository=associationRepository;
		this.processRequestReadplatformService=processRequestReadplatformService;
		

	}

	@Override
	public CommandProcessingResult createProvisioning(JsonCommand command) {
		
			try{	
				 this.context.authenticatedUser();
				 this.fromApiJsonDeserializer.validateForProvisioning(command.json());
				 ProvisioningCommand provisioningCommand=ProvisioningCommand.from(command);
				 
				 final JsonElement element = fromApiJsonHelper.parse(command.json());
				 final JsonArray commandArray=fromApiJsonHelper.extractJsonArrayNamed("commandParameters",element);
				 if(commandArray!=null){
			     for (JsonElement jsonelement : commandArray) {	
				          String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);		    
				          String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);	
				          String paramDefault=null;
				          if(fromApiJsonHelper.parameterExists("paramDefault", jsonelement)){
				        	  paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);	
				          }
				          ProvisioningCommandParameters data=new ProvisioningCommandParameters(commandParam,paramType,paramDefault);
				          provisioningCommand.addCommandParameters(data);
			     }
			     }
			     
			     this.provisioningCommandRepository.save(provisioningCommand);
			     
			     return new CommandProcessingResult(provisioningCommand.getId());	
			     
			}catch (DataIntegrityViolationException dve) {
				handleCodeDataIntegrityIssues(command, dve);
				return new CommandProcessingResult(Long.valueOf(-1));
			}
			
		
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
	}

	@Override
	public CommandProcessingResult updateProvisioning(JsonCommand command) {

		try{	
			 this.context.authenticatedUser();
			 this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			 ProvisioningCommand provisioningCommand= this.provisioningCommandRepository.findOne(command.entityId());
			 provisioningCommand.getCommandparameters().clear();
			 
			 final Map<String, Object> changes = provisioningCommand.UpdateProvisioning(command);
			 
			 final JsonElement element = fromApiJsonHelper.parse(command.json());
			 final JsonArray commandArray=fromApiJsonHelper.extractJsonArrayNamed("commandParameters",element);
			 if(commandArray!=null){
		     for (JsonElement jsonelement : commandArray) {	
			          String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);		    
			          String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);	
			          String paramDefault=null;
			          if(fromApiJsonHelper.parameterExists("paramDefault", jsonelement)){
			        	  paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);	
			          }
			          ProvisioningCommandParameters data=new ProvisioningCommandParameters(commandParam,paramType,paramDefault);
			          provisioningCommand.addCommandParameters(data);
		     }
		     }
		     
		     this.provisioningCommandRepository.save(provisioningCommand);
		     
		     return new CommandProcessingResult(provisioningCommand.getId());	
		     
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult deleteProvisioningSystem(JsonCommand command) {
		try{	
			 this.context.authenticatedUser();
			 ProvisioningCommand provisioningCommand= this.provisioningCommandRepository.findOne(command.entityId());
			 if(provisioningCommand.getIsDeleted()!='Y')
			 {
				 provisioningCommand.setIsDeleted('Y');
			 }
		     this.provisioningCommandRepository.save(provisioningCommand);
		     
		     return new CommandProcessingResult(provisioningCommand.getId());	
		     
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

    @Transactional
	@Override
	public CommandProcessingResult updateProvisioningDetails(Long entityId) {
		
	   try{
			this.context.authenticatedUser();
			ProcessRequest processRequest=this.processRequestRepository.findOne(entityId);
			/*List<ProcessRequestDetails> details=processRequest.getProcessRequestDetails();
			
			GlobalConfigurationProperty configurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
			if(!details.isEmpty()){
			String oldHardWare=details.get(0).getHardwareId();
			final String newHardwareId=command.stringValueOfParameterNamed("hardwareId");
			
			  if (!oldHardWare.equalsIgnoreCase(newHardwareId)) {
		        Long  id=this.provisioningReadPlatformService.getHardwareDetails(oldHardWare,processRequest.getClientId(),configurationProperty.getValue());
		        
		        if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
		        	
		        	OwnedHardware ownedHardware=this.hardwareJpaRepository.findOne(id);
		        	
		        	ownedHardware.updateSerialNumbers(newHardwareId);
		        	this.hardwareJpaRepository.saveAndFlush(ownedHardware);
		        	
		        }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
		        	
		        	InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.findOne(id);
		        	inventoryItemDetails.setProvisioningSerialNumber(newHardwareId);
		        	this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
		        }
				    				  
				  
		        }
			}
                  
			for(ProcessRequestDetails processRequestDetails:details){
				processRequestDetails.updateDetails(command);
			}*/
			if(processRequest != null){
			processRequest.update();
			this.processRequestRepository.saveAndFlush(processRequest);
			}
			
		return new CommandProcessingResult(entityId);	
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}

    @Transactional
	@Override
	public void updateHardwareDetails(Long clientId, String serialNumber,String oldSerialnumber,String provSerilaNum,String oldHardware) {
		
		Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,oldSerialnumber);
	    if(activeorders!= 0){
		   throw new ActivePlansFoundException(serialNumber);
	   }
	   
	  //Update in Association table if Exist
	   HardwareAssociation hardwareAssociation=this.associationRepository.findOneByserialNo(oldSerialnumber);
	   if(hardwareAssociation != null){
	   hardwareAssociation.updateserailNum(serialNumber);
	   this.associationRepository.saveAndFlush(hardwareAssociation);
	   }
	   
	 //Update ProcessRequest
	   final Long ProcessReqId=this.processRequestReadplatformService.retrievelatestReqId(clientId,oldHardware);
	   
	   if(ProcessReqId != null && !ProcessReqId.equals(new Long(0))){
		   
		   ProcessRequest processRequest=this.processRequestRepository.findOne(ProcessReqId);
		   
		   List<ProcessRequestDetails> processRequestDetails=processRequest.getProcessRequestDetails();
		   
		   for(ProcessRequestDetails details:processRequestDetails){
			   details.update(provSerilaNum);
		   }
		   
		 
		   this.processRequestRepository.saveAndFlush(processRequest);
		   
	   }
	   
		
	}

	
}

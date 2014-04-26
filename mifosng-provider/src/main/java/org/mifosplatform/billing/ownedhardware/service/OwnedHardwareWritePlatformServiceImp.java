package org.mifosplatform.billing.ownedhardware.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.association.data.HardwareAssociationData;
import org.mifosplatform.billing.association.domain.HardwareAssociation;
import org.mifosplatform.billing.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.billing.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.billing.inventory.exception.ActivePlansFoundException;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsReadPlatformService;
import org.mifosplatform.billing.order.domain.HardwareAssociationRepository;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.ownedhardware.data.OwnedHardware;
import org.mifosplatform.billing.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.billing.ownedhardware.serialization.OwnedHardwareFromApiJsonDeserializer;
import org.mifosplatform.billing.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.codes.exception.DiscountNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnedHardwareWritePlatformServiceImp implements OwnedHardwareWritePlatformService{

	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(OwnedHardwareWritePlatformServiceImp.class);
	
	private final OwnedHardwareJpaRepository ownedHardwareJpaRepository;
	private final PlatformSecurityContext context;
	private final OwnedHardwareFromApiJsonDeserializer apiJsonDeserializer;
	private final OwnedHardwareReadPlatformService ownedHardwareReadPlatformService;
	private final InventoryItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService;
	private final GlobalConfigurationRepository globalConfigurationRepository;
	private final HardwareAssociationWriteplatformService hardwareAssociationWriteplatformService;
	private final HardwareAssociationReadplatformService associationReadplatformService;
	private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private final OrderReadPlatformService orderReadPlatformService; 
	private final HardwareAssociationRepository associationRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	
	
	@Autowired
	public OwnedHardwareWritePlatformServiceImp(final OwnedHardwareJpaRepository ownedHardwareJpaRepository, final PlatformSecurityContext context,
			final OwnedHardwareFromApiJsonDeserializer apiJsonDeserializer,final OwnedHardwareReadPlatformService ownedHardwareReadPlatformService,
			final InventoryItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService,final GlobalConfigurationRepository globalConfigurationRepository,
			final HardwareAssociationWriteplatformService hardwareAssociationWriteplatformService,final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OrderReadPlatformService orderReadPlatformService,
			final HardwareAssociationRepository associationRepository,final ProvisioningWritePlatformService provisioningWritePlatformService) {
		this.ownedHardwareJpaRepository = ownedHardwareJpaRepository;
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.ownedHardwareReadPlatformService = ownedHardwareReadPlatformService;
		this.inventoryItemDetailsReadPlatformService = inventoryItemDetailsReadPlatformService;
		this.globalConfigurationRepository=globalConfigurationRepository;
		this.hardwareAssociationWriteplatformService=hardwareAssociationWriteplatformService;
		this.associationReadplatformService=hardwareAssociationReadplatformService;
		this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
		this.orderReadPlatformService=orderReadPlatformService;
		this.associationRepository=associationRepository;
		this.provisioningWritePlatformService=provisioningWritePlatformService;
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult createOwnedHardware(JsonCommand command, final Long clientId) {
	OwnedHardware ownedHardware = null;
	
	try{	
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		ownedHardware = OwnedHardware.fromJson(command,clientId);
		List<String> inventorySerialNumbers = inventoryItemDetailsReadPlatformService.retriveSerialNumbers();
		List<String> ownedhardwareSerialNumbers = ownedHardwareReadPlatformService.retriveSerialNumbers();
		String ownedHardwareSerialNumber = ownedHardware.getSerialNumber();
		
		if(inventorySerialNumbers.contains(ownedHardwareSerialNumber) | ownedhardwareSerialNumbers.contains(ownedHardwareSerialNumber)){
			throw new PlatformDataIntegrityException("validation.error.msg.ownedhardware.duplicate.serialNumber","validation.error.msg.ownedhardware.duplicate.serialNumber","serialNumber","");
		}
		
		this.ownedHardwareJpaRepository.save(ownedHardware);
		
		  //For Plan And HardWare Association
		GlobalConfigurationProperty configurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IMPLICIT_ASSOCIATION);
		
		if(configurationProperty.isEnabled()){
			
			configurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
			
			if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			
		           List<HardwareAssociationData> allocationDetailsDatas=this.associationReadplatformService.retrieveClientAllocatedPlan(ownedHardware.getClientId(),ownedHardware.getItemType());
		    
		        if(!allocationDetailsDatas.isEmpty())
		    		   {
		    				this.hardwareAssociationWriteplatformService.createNewHardwareAssociation(ownedHardware.getClientId(),allocationDetailsDatas.get(0).getPlanId(),ownedHardware.getSerialNumber(),allocationDetailsDatas.get(0).getorderId());
		    				transactionHistoryWritePlatformService.saveTransactionHistory(ownedHardware.getClientId(), "Association", new Date(),"Serial No:"
		    				+ownedHardware.getSerialNumber(),"Item Code:"+allocationDetailsDatas.get(0).getItemCode());
		    				
		    		   }
		    }
		}
		return new CommandProcessingResultBuilder().withEntityId(ownedHardware.getId()).build();
		
	}catch(DataIntegrityViolationException dve){
		handleDataIntegrityIssues(command, dve);
		return CommandProcessingResult.empty();
	}
		
	}
	
	

	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("serialNumber")) {
            final String name = command.stringValueOfParameterNamed("serialNumber");
            throw new PlatformDataIntegrityException("error.msg.serialnumber.duplicate.name", "serialnumber with name `" + name + "` already exists",
                    "serialnumber", name);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.charge.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

	@Transactional
	@Override
	public CommandProcessingResult updateOwnedHardware(JsonCommand command,Long id)
	{
        try{
        	  
        	this.context.authenticatedUser();
        	this.apiJsonDeserializer.validateForCreate(command.json());
        	
        	OwnedHardware ownedHardware=OwnedHardwareretrieveById(id);
        	
        	final String oldHardware=ownedHardware.getSerialNumber();
        	final Map<String, Object> changes = ownedHardware.update(command); 
        	
        	if(!changes.isEmpty()){
        		this.ownedHardwareJpaRepository.save(ownedHardware);
        	}
        
        	if(!oldHardware.equalsIgnoreCase(ownedHardware.getSerialNumber())){
        	  
        		this.provisioningWritePlatformService.updateHardwareDetails(ownedHardware.getClientId(),ownedHardware.getSerialNumber(),
        				ownedHardware.getProvisioningSerialNumber(),oldHardware);
        		
        	}
        	
        	return new CommandProcessingResult(id);
        	
        }catch(DataIntegrityViolationException dve){
        	handleDataIntegrityIssues(command, dve);
        	return CommandProcessingResult.empty();     
        	}
		
         
}

	private OwnedHardware OwnedHardwareretrieveById(Long id) {
             
		OwnedHardware ownedHardware=this.ownedHardwareJpaRepository.findOne(id);
              if (ownedHardware== null) { throw new DiscountNotFoundException(id.toString()); }
	          return ownedHardware;	
	}

	@Override
	public CommandProcessingResult deleteOwnedHardware(Long id) {

     try{
    	 this.context.authenticatedUser();
    	 OwnedHardware ownedHardware=this.ownedHardwareJpaRepository.findOne(id);
    	 
    	 if(ownedHardware==null){
    		 throw new DiscountNotFoundException(id.toString());
    	 }
    	 
    	 //Check if Active plans are exist
    	 Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(ownedHardware.getClientId(),ownedHardware.getSerialNumber());
  	   if(activeorders!= 0){
  		   throw new ActivePlansFoundException();
  	   }
    	 
    	
    	 ownedHardware.delete();
    	 this.ownedHardwareJpaRepository.save(ownedHardware);
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(DataIntegrityViolationException exception){
    	 return new CommandProcessingResult(Long.valueOf(-1));
     }
	}
}

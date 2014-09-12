package org.mifosplatform.logistics.ownedhardware.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.discountmaster.exceptions.DiscountNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.itemdetails.service.InventoryItemDetailsReadPlatformService;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.mifosplatform.logistics.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.logistics.ownedhardware.exception.ActiveDeviceExceedException;
import org.mifosplatform.logistics.ownedhardware.serialization.OwnedHardwareFromApiJsonDeserializer;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.portfolio.order.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
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
	public static final String ACTIVE_DEVICE="Active Devices"; 
	
	
	@Autowired
	public OwnedHardwareWritePlatformServiceImp(final OwnedHardwareJpaRepository ownedHardwareJpaRepository, final PlatformSecurityContext context,
			final OwnedHardwareFromApiJsonDeserializer apiJsonDeserializer,final OwnedHardwareReadPlatformService ownedHardwareReadPlatformService,
			final InventoryItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService,final GlobalConfigurationRepository globalConfigurationRepository,
			final HardwareAssociationWriteplatformService hardwareAssociationWriteplatformService,final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OrderReadPlatformService orderReadPlatformService,
			final HardwareAssociationRepository associationRepository,final ProvisioningWritePlatformService provisioningWritePlatformService) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.associationRepository=associationRepository;
		this.orderReadPlatformService=orderReadPlatformService;
		this.ownedHardwareJpaRepository = ownedHardwareJpaRepository;
		this.globalConfigurationRepository=globalConfigurationRepository;
		this.provisioningWritePlatformService=provisioningWritePlatformService;
		this.ownedHardwareReadPlatformService = ownedHardwareReadPlatformService;
		this.associationReadplatformService=hardwareAssociationReadplatformService;
        this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
		this.inventoryItemDetailsReadPlatformService = inventoryItemDetailsReadPlatformService;
		this.hardwareAssociationWriteplatformService=hardwareAssociationWriteplatformService;
		
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult createOwnedHardware(JsonCommand command, final Long clientId) {
	OwnedHardware ownedHardware = null;
	
	try{	
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
	
		boolean isCheck=this.checkforClientActiveDevices(clientId);
	
		if(!isCheck){
			throw new ActiveDeviceExceedException(clientId);
		}
		
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
		/*GlobalConfigurationProperty balanceCheckconfigurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
        ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);

        String balanceCheck="N";
        if(configurationProperty.isEnabled()){
        	balanceCheck="Y";
        }*/
		return new CommandProcessingResultBuilder().withEntityId(ownedHardware.getId()).build();
		
	}catch(DataIntegrityViolationException dve){
		handleDataIntegrityIssues(command, dve);
		return CommandProcessingResult.empty();
	}
		
	}
	
	

	
	private boolean checkforClientActiveDevices(Long clientId) {
		
		boolean isCheck=true;
		GlobalConfigurationProperty configurationProperty=this.globalConfigurationRepository.findOneByName(ACTIVE_DEVICE);
		
		if(configurationProperty.isEnabled()){
			int clientDevices=this.ownedHardwareReadPlatformService.retrieveClientActiveDevices(clientId);
			
			if(clientDevices >= Integer.parseInt(configurationProperty.getValue())){
				isCheck=false;
			}
		}
		   return isCheck; 
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
        	
        	final String oldHardware=ownedHardware.getProvisioningSerialNumber();
        	final String oldSerialnumber=ownedHardware.getSerialNumber();
        	final Map<String, Object> changes = ownedHardware.update(command); 
        	
        	if(!changes.isEmpty()){
        		this.ownedHardwareJpaRepository.save(ownedHardware);
        	}
        
        	if(!oldHardware.equalsIgnoreCase(ownedHardware.getProvisioningSerialNumber())){
        	  
        		this.provisioningWritePlatformService.updateHardwareDetails(ownedHardware.getClientId(),ownedHardware.getSerialNumber(),oldSerialnumber,
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
    	 List<HardwareAssociation> hardwareAssociations=this.associationRepository.findOneByserialNo(ownedHardware.getSerialNumber());
    	 
    	 if(!hardwareAssociations.isEmpty()){
    		 
    		 for(HardwareAssociation hardwareAssociation:hardwareAssociations){
    		 hardwareAssociation.delete();
    		 this.associationRepository.save(hardwareAssociation);
    		 
    		 }
    	 }
    	 
    	 return new CommandProcessingResult(id);
    	 
    	 
     }catch(DataIntegrityViolationException exception){
    	 return new CommandProcessingResult(Long.valueOf(-1));
     }
	}
}

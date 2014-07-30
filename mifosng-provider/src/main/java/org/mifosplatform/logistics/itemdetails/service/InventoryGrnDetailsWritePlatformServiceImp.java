package org.mifosplatform.logistics.itemdetails.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrn;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryGrnCommandFromApiJsonDeserializer;
import org.mifosplatform.workflow.eventactionmapping.exception.EventActionMappingNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryGrnDetailsWritePlatformServiceImp implements InventoryGrnDetailsWritePlatformService{

	
	private PlatformSecurityContext context;
	private InventoryGrnRepository inventoryGrnRepository;
	private InventoryGrnCommandFromApiJsonDeserializer inventoryGrnCommandFromApiJsonDeserializer;
	
	@Autowired
	public InventoryGrnDetailsWritePlatformServiceImp(final PlatformSecurityContext context,InventoryGrnRepository inventoryGrnRepository, InventoryGrnCommandFromApiJsonDeserializer inventoryGrnCommandFromApiJsonDeserializer) {
		this.context = context;
		this.inventoryGrnRepository = inventoryGrnRepository;
		this.inventoryGrnCommandFromApiJsonDeserializer = inventoryGrnCommandFromApiJsonDeserializer;
	}
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(InventoryGrnDetailsWritePlatformServiceImp.class);	
		
		
	@Transactional
	@Override
	public CommandProcessingResult addGrnDetails(JsonCommand command) {
		InventoryGrn inventoryGrn = null;
		try{
			context.authenticatedUser();
			inventoryGrnCommandFromApiJsonDeserializer.validateForCreate(command.json());
			inventoryGrn = InventoryGrn.fromJson(command);
			this.inventoryGrnRepository.save(inventoryGrn);
		}catch(DataIntegrityViolationException dve){
			logger.error(dve.getMessage(), dve);   
			return new CommandProcessingResult(Long.valueOf(-1));
		
		}
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(inventoryGrn.getId()).build();
	}

	private InventoryGrn grnRetrieveById(Long id) {
        
		InventoryGrn grnId=this.inventoryGrnRepository.findOne(id);
              if (grnId== null) { throw new EventActionMappingNotFoundException(id.toString()); }
	          return grnId;	
	}

	@Override
	public CommandProcessingResult editGrnDetails(JsonCommand command,
			Long entityId) {
		
		try{
			context.authenticatedUser();
			inventoryGrnCommandFromApiJsonDeserializer.validateForCreate(command.json());
			InventoryGrn inventoryGrnDetails=grnRetrieveById(entityId);
			
			final Map<String, Object> changes = inventoryGrnDetails.update(command);
			
			if(!changes.isEmpty()){
        		this.inventoryGrnRepository.save(inventoryGrnDetails);
        	}
			
			return new CommandProcessingResult(entityId);
		}catch(DataIntegrityViolationException dve){
			logger.error(dve.getMessage(), dve);   
			return new CommandProcessingResult(Long.valueOf(-1));
		
		}
	}


     	
}

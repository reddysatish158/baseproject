package org.mifosplatform.logistics.item.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemAuditRepository;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemMasterAudit;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.item.serialization.ItemCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemWritePlatformServiceImpl implements ItemWritePlatformService{

	private ItemCommandFromApiJsonDeserializer itemCommandFromApiJsonDeserializer; 
	private final PlatformSecurityContext context;
	private final ItemRepository itemRepository;
	private final ItemAuditRepository itemAuditRepository;
	
 @Autowired
 public ItemWritePlatformServiceImpl(final PlatformSecurityContext context,
		 final ItemRepository itemrepository,final ItemCommandFromApiJsonDeserializer itemCommandFromApiJsonDeserializer,
		 final ItemAuditRepository itemAuditRepository){
	 this.context=context;
	 this.itemRepository=itemrepository;
	 this.itemCommandFromApiJsonDeserializer = itemCommandFromApiJsonDeserializer;
	 this.itemAuditRepository=itemAuditRepository;
 }
	
    @Transactional
	@Override
	public CommandProcessingResult createItem(JsonCommand command) {
  
    try{	 
    this.context.authenticatedUser();
    this.itemCommandFromApiJsonDeserializer.validateForCreate(command.json());
    ItemMaster itemMaster=new ItemMaster(command.stringValueOfParameterNamed("itemCode"),command.stringValueOfParameterNamed("itemDescription"),command.stringValueOfParameterNamed("itemClass"),command.bigDecimalValueOfParameterNamed("unitPrice"),command.stringValueOfParameterNamed("units"),command.longValueOfParameterNamed("warranty"),command.stringValueOfParameterNamed("chargeCode")); 
    this.itemRepository.save(itemMaster);
    return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(itemMaster.getId()).build();
    } catch (DataIntegrityViolationException dve) {
    	handleItemDataIntegrityIssues(command, dve);
        return CommandProcessingResult.empty();
    }
    }


	
    private void handleItemDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("code_name_org")) {
            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
        
        }else  if (realCause.getMessage().contains("item_code")) {
            final String name = command.stringValueOfParameterNamed("itemCode");
            throw new PlatformDataIntegrityException("error.msg.item.code.duplicate.name", "A Item code with name '" + name + "' already exists");
        }

        //logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

	@Override
	public CommandProcessingResult updateItem(JsonCommand command, Long itemId) {
   try{
	   
	   this.context.authenticatedUser();
	   this.itemCommandFromApiJsonDeserializer.validateForCreate(command.json());
	   
	   ItemMaster itemMaster = retrieveCodeBy(itemId);
	   final int unitPrice = command.integerValueOfParameterNamed("unitPrice");
	   int existingUnitPrice = itemMaster.getUnitPrice().intValueExact();
	   if(unitPrice!=existingUnitPrice){
		   
		   ItemMasterAudit itemMasterAudit = new ItemMasterAudit(itemId,existingUnitPrice,command);
		   this.itemAuditRepository.save(itemMasterAudit);
		  
	   }
	   final Map<String, Object> changes = itemMaster.update(command);
	   
	   if(changes.isEmpty()){
		   itemRepository.save(itemMaster);
	   }
	 
	   return new CommandProcessingResultBuilder() //
       .withCommandId(command.commandId()) //
       .withEntityId(itemId) //
       .with(changes) //
       .build();
	}catch (DataIntegrityViolationException dve) {
	      handleItemDataIntegrityIssues(command, dve);
	      return new CommandProcessingResult(Long.valueOf(-1));
	  }

}

	@Override
	public CommandProcessingResult deleteItem(Long itemId) {
		try{
			this.context.authenticatedUser();
			ItemMaster itemMaster=retrieveCodeBy(itemId);
			if(itemMaster.getDeleted()=='Y'){
				throw new ItemNotFoundException(itemId.toString());
			}
			itemMaster.delete();
			this.itemRepository.save(itemMaster);
			return new CommandProcessingResultBuilder().withEntityId(itemId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleItemDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
	
	private ItemMaster retrieveCodeBy(final Long itemId) {
        final ItemMaster item = this.itemRepository.findOne(itemId);
        if (item == null) { throw new ItemNotFoundException(itemId.toString()); }
        return item;
    }
}

package org.mifosplatform.logistics.agent.service;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoice;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoiceRepository;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.agent.serialization.AgentItemSaleCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AgentWriteplatformServiceImpl implements AgentWriteplatformService{

	private final PlatformSecurityContext context;
	private final ItemSaleRepository itemSaleRepository;
	private final ItemSaleInvoiceRepository itemSaleInvoiceRepository;
	private final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	
	@Autowired
	public AgentWriteplatformServiceImpl(final PlatformSecurityContext context,final ItemSaleRepository itemSaleRepository,
			final ItemSaleInvoiceRepository itemSaleInvoiceRepository,final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer){
		
		   this.context=context;
		   this.itemSaleRepository=itemSaleRepository;
		   this.itemSaleInvoiceRepository=itemSaleInvoiceRepository;
		   this.apiJsonDeserializer=apiJsonDeserializer;
		
	}
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult createNewItemSale(JsonCommand command) {

        try{
        	
        	this.context.authenticatedUser();
        	this.apiJsonDeserializer.validateForCreate(command.json());
        	ItemSale itemSale=ItemSale.fromJson(command);
        	
        	
        	ItemSaleInvoice invoice=ItemSaleInvoice.fromJson(command);
        	BigDecimal taxAmount=this.calculateTaxAmount(invoice.getChargeAmount(),invoice.getTaxPercantage());

        	invoice.updateAmounts(taxAmount);
        	itemSale.setItemSaleInvoice(invoice);
        	
        	this.itemSaleRepository.save(itemSale);
           return new CommandProcessingResult(itemSale.getId());        	
        }catch(DataIntegrityViolationException dve){
        	handleCodeDataIntegrityIssues(command, dve);
        	return new CommandProcessingResult(new Long(-1));
        	
        }
		
		
	}


	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
	
		Throwable realCause = dve.getMostSpecificCause();
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

	private BigDecimal calculateTaxAmount(BigDecimal chargeAmount,BigDecimal taxPercantage) {
		
		BigDecimal taxAmount=BigDecimal.ZERO;
		taxAmount=chargeAmount.multiply(taxPercantage.divide(new BigDecimal(100)));
		return taxAmount;
		
		
		
	}

}

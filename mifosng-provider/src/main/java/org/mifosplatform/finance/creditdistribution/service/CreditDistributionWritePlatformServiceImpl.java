package org.mifosplatform.finance.creditdistribution.service;

import java.math.BigDecimal;

import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.domain.InvoiceRepository;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistribution;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistributionRepository;
import org.mifosplatform.finance.creditdistribution.serialization.CreditDistributionCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.domain.PaymentRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.service.PlanWritePlatformServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class CreditDistributionWritePlatformServiceImpl implements CreditDistributionWritePlatformService{

	private final static Logger logger = LoggerFactory.getLogger(PlanWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final CreditDistributionRepository creditDistributionRepository;
	private final CreditDistributionCommandFromApiJsonDeserializer commandFromApiJsonDeserializer; 
	private final FromJsonHelper fromJsonHelper;
	private final InvoiceRepository invoiceRepository;
	private final PaymentRepository paymentRepository;
	
	
	@Autowired
	public CreditDistributionWritePlatformServiceImpl(PlatformSecurityContext context,final CreditDistributionRepository creditDistributionRepository,
			final CreditDistributionCommandFromApiJsonDeserializer commandFromApiJsonDeserializer,final FromJsonHelper fromJsonHelper,
			final InvoiceRepository invoiceRepository,final PaymentRepository paymentRepository){
		
		this.context=context;
		this.creditDistributionRepository=creditDistributionRepository;
		this.commandFromApiJsonDeserializer=commandFromApiJsonDeserializer;
		this.fromJsonHelper=fromJsonHelper;
		this.invoiceRepository=invoiceRepository;
		this.paymentRepository=paymentRepository;
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult createCreditDistribution(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			commandFromApiJsonDeserializer.validateForCreate(command.json());
			 final JsonElement element = fromJsonHelper.parse(command.json());
			 final BigDecimal avialableAmount = fromJsonHelper.extractBigDecimalWithLocaleNamed("avialableAmount",element);
			 final Long paymentId=command.longValueOfParameterNamed("paymentId");
		     JsonArray creditDistributions = fromJsonHelper.extractJsonArrayNamed("creditdistributions", element);
		        
		        for(JsonElement j:creditDistributions)
		        {
		        	
		        	CreditDistribution creditDistribution= CreditDistribution.fromJson(j,fromJsonHelper);
		        	this.creditDistributionRepository.save(creditDistribution);
		        	Invoice invoice=this.invoiceRepository.findOne(creditDistribution.getInvoiceId());
		        	invoice.updateAmount(creditDistribution.getAmount());
		        	this.invoiceRepository.save(invoice);
		        	
		        }
		        
		      if(avialableAmount.compareTo(BigDecimal.ZERO) !=1){
		    	  Payment payment=this.paymentRepository.findOne(paymentId);
		    	  payment.setInvoiceId(Long.valueOf(-1));
		    	  this.paymentRepository.save(payment);
		    	  
		      }
		    return new CommandProcessingResult(Long.valueOf(1l));
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve); 
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
	}


	private void handleDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

}

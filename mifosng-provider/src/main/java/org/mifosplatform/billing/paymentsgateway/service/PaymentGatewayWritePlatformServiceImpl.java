package org.mifosplatform.billing.paymentsgateway.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.mifosplatform.billing.payments.service.PaymentWritePlatformService;
import org.mifosplatform.billing.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.billing.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.billing.paymentsgateway.serialization.PaymentGatewayCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


@Service
public class PaymentGatewayWritePlatformServiceImpl implements PaymentGatewayWritePlatformService {

	
	    private final PlatformSecurityContext context;
	    private final PaymentGatewayRepository paymentGatewayRepository;
	    private final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer;
	    private final FromJsonHelper fromApiJsonHelper;
	    private final PaymentGatewayReadPlatformService readPlatformService;
	    private final PaymentWritePlatformService paymentWritePlatformService;
	    private final PaymodeReadPlatformService paymodeReadPlatformService;
	   
	    @Autowired
	    public PaymentGatewayWritePlatformServiceImpl(final PlatformSecurityContext context,
	    	    final PaymentGatewayRepository paymentGatewayRepository,final FromJsonHelper fromApiJsonHelper,
	    		final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer,
	    		final PaymentGatewayReadPlatformService readPlatformService,
	    		final PaymentWritePlatformService paymentWritePlatformService,
	    		final PaymodeReadPlatformService paymodeReadPlatformService)
	    {
	    	this.context=context;
	    	this.paymentGatewayRepository=paymentGatewayRepository;
	    	this.fromApiJsonHelper=fromApiJsonHelper;
	    	this.paymentGatewayCommandFromApiJsonDeserializer=paymentGatewayCommandFromApiJsonDeserializer;
	    	this.readPlatformService=readPlatformService;
	    	this.paymentWritePlatformService=paymentWritePlatformService;
	    	 this.paymodeReadPlatformService=paymodeReadPlatformService;
	    }

		@Override
		public CommandProcessingResult createPaymentGateway(JsonCommand command) {
			  JsonElement element=null;
			  CommandProcessingResult result=null;
			try {
				   context.authenticatedUser();
				   this.paymentGatewayCommandFromApiJsonDeserializer.validateForCreate(command.json());
				   element= fromApiJsonHelper.parse(command.json());
				   if(element!=null){
					    String serialNumberId = fromApiJsonHelper.extractStringNamed("reference", element);
					    String paymentDate = fromApiJsonHelper.extractStringNamed("timestamp", element);
					    BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
					    String phoneNo = fromApiJsonHelper.extractStringNamed("msisdn", element);
					    String receiptNo = fromApiJsonHelper.extractStringNamed("receipt", element);
					    String SOURCE = fromApiJsonHelper.extractStringNamed("service", element);
					    String details = fromApiJsonHelper.extractStringNamed("name", element);
					    DateFormat readFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
					    Date date = readFormat.parse(paymentDate);					     
					    PaymentGateway  paymentGateway = new PaymentGateway(serialNumberId,phoneNo,date,amountPaid,receiptNo,SOURCE,details);
					    this.paymentGatewayRepository.save(paymentGateway);
					
					    Long clientId = this.readPlatformService.retrieveClientIdForProvisioning(serialNumberId);
						if(clientId == null){
							PaymentGateway gateway=this.paymentGatewayRepository.findOne(paymentGateway.getId());
					    	gateway.setStatus("Failure");    	
					    	this.paymentGatewayRepository.save(gateway);
					    	return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withTransactionId("Failure").build();		
						}else{
							Long paymodeId=this.paymodeReadPlatformService.getOnlinePaymode();
							if(paymodeId==null){
								paymodeId=new Long(27);
							}
							String remarks="customerName: "+details+" ,PhoneNo:"+phoneNo+" ,Biller account Name : "+SOURCE;
							SimpleDateFormat daformat=new SimpleDateFormat("dd MMMM yyyy");
						    String paymentdate=daformat.format(date);
						    JsonObject object=new JsonObject();
						    object.addProperty("dateFormat","dd MMMM yyyy");
						    object.addProperty("locale","en");
						    object.addProperty("paymentDate",paymentdate);
						    object.addProperty("amountPaid",amountPaid);
						    object.addProperty("isChequeSelected","no");
						    object.addProperty("receiptNo",receiptNo);
						    object.addProperty("remarks",remarks);
						    object.addProperty("paymentCode",paymodeId);
						    String entityName="PAYMENT";
						    final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
						    JsonCommand comm=new JsonCommand(null, object.toString(), element1, fromApiJsonHelper, entityName, clientId, null, null, null,
					                null, null, null, null, null, null);
						    result=this.paymentWritePlatformService.createPayment(comm);
						    if(result.resourceId()!=null){
						    	PaymentGateway gateway=this.paymentGatewayRepository.findOne(paymentGateway.getId());
						    	gateway.setObsId(result.resourceId());
						    	gateway.setStatus("Success");    
						    	gateway.setAuto(false);
						    	this.paymentGatewayRepository.save(gateway);	    	
						    }						    
						}					  		               
				   }	 
				   return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(result.resourceId()).withTransactionId("Success").build();
			} catch (DataIntegrityViolationException dve) {
	           // return CommandProcessingResult.empty();
				    handleCodeDataIntegrityIssues(element, dve);
					return new CommandProcessingResult(Long.valueOf(-1));
	        }catch ( ParseException e ) {
	        	    return new CommandProcessingResult(Long.valueOf(-1));
		    }
			
			
		}
		
		private void handleCodeDataIntegrityIssues(JsonElement element,
				DataIntegrityViolationException dve) {
			
			 Throwable realCause = dve.getMostSpecificCause();
		        if (realCause.getMessage().contains("reference")) {
		            final String name = fromApiJsonHelper.extractStringNamed("reference", element);
		            throw new PlatformDataIntegrityException("error.msg.code.reference", "A reference with this value '" + name + "' does not exists");
		        }
		        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
		                "Unknown data integrity issue with resource: " + realCause.getMessage());
			
		}

		@Override
		public CommandProcessingResult updatePaymentGateway(JsonCommand command) {
			
			this.context.authenticatedUser();
			this.paymentGatewayCommandFromApiJsonDeserializer.validateForUpdate(command.json());
			PaymentGateway gateway=this.paymentGatewayRepository.findOne(command.entityId());
			final Map<String, Object> changes =gateway.fromJson(command);
			this.paymentGatewayRepository.save(gateway);	   
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(gateway.getId()).with(changes).build();
		}
			 
	    	

	
	
}

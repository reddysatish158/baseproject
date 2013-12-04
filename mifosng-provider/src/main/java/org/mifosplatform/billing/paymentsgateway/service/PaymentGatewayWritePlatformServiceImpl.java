package org.mifosplatform.billing.paymentsgateway.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mifosplatform.billing.mediadevice.data.MediaDeviceData;
import org.mifosplatform.billing.mediadevice.service.MediaDeviceReadPlatformService;
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
	    private final MediaDeviceReadPlatformService mediaDeviceReadPlatformService;
	    private final PaymentWritePlatformService paymentWritePlatformService;
	    private final PaymodeReadPlatformService paymodeReadPlatformService;
	   
	    @Autowired
	    public PaymentGatewayWritePlatformServiceImpl(final PlatformSecurityContext context,
	    	    final PaymentGatewayRepository paymentGatewayRepository,final FromJsonHelper fromApiJsonHelper,
	    		final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer,
	    		final MediaDeviceReadPlatformService mediaDeviceReadPlatformService,
	    		final PaymentWritePlatformService paymentWritePlatformService,
	    		final PaymodeReadPlatformService paymodeReadPlatformService)
	    {
	    	this.context=context;
	    	this.paymentGatewayRepository=paymentGatewayRepository;
	    	this.fromApiJsonHelper=fromApiJsonHelper;
	    	this.paymentGatewayCommandFromApiJsonDeserializer=paymentGatewayCommandFromApiJsonDeserializer;
	    	this.mediaDeviceReadPlatformService=mediaDeviceReadPlatformService;
	    	this.paymentWritePlatformService=paymentWritePlatformService;
	    	 this.paymodeReadPlatformService=paymodeReadPlatformService;
	    }

		@Override
		public CommandProcessingResult createPaymentGateway(JsonCommand command) {
			  JsonElement element=null;
			try {
				   context.authenticatedUser();
				   this.paymentGatewayCommandFromApiJsonDeserializer.validateForCreate(command.json());
				   element= fromApiJsonHelper.parse(command.json());
				   if(element!=null){
				    String keyId = fromApiJsonHelper.extractStringNamed("KEY_ID", element);
				    String paymentDate = fromApiJsonHelper.extractStringNamed("PAYMENT_DATE", element);
				    BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("AMOUNT_PAID", element);
				    String partyId = fromApiJsonHelper.extractStringNamed("PARTY_ID", element);
				    String receiptNo = fromApiJsonHelper.extractStringNamed("RECEIPT_NO", element);
				    String SOURCE = fromApiJsonHelper.extractStringNamed("SOURCE", element);
				    String paymentId = fromApiJsonHelper.extractStringNamed("PAYMENT_ID", element);
				    String details = fromApiJsonHelper.extractStringNamed("DETIALS", element);
				   DateFormat readFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					  Date date = null;
					    try {
					       date = readFormat.parse(paymentDate);
					    } catch ( ParseException e ) {
					        e.printStackTrace();
					    }
				   PaymentGateway  paymentGateway = new PaymentGateway(keyId,partyId,date,amountPaid,receiptNo,SOURCE,paymentId,details);
				   this.paymentGatewayRepository.save(paymentGateway);
				
				   MediaDeviceData datas = this.mediaDeviceReadPlatformService.retrieveDeviceDetails(keyId);
					if(datas == null){
						throw new IllegalAccessException();		
					}
					Long paymodeId=this.paymodeReadPlatformService.getOnlinePaymode();
					if(paymodeId==null){
						paymodeId=new Long(27);
					}
					Long id=datas.getClientId();
					String remarks="Details: "+details+" ,PARTY_ID:"+partyId+" ,SOURCE : "+SOURCE;
					SimpleDateFormat daformat=new SimpleDateFormat("dd MMMM yyyy");
				    String paymentdate=daformat.format(date);
				    JsonObject object=new JsonObject();
				    object.addProperty("txn_id", paymentId);
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
				    JsonCommand comm=new JsonCommand(null, object.toString(), element1, fromApiJsonHelper, entityName, id, null, null, null,
			                null, null, null, null, null, null);
				    CommandProcessingResult result=this.paymentWritePlatformService.createPayment(comm);
				    if(result.resourceId()!=null){
				    	PaymentGateway gateway=this.paymentGatewayRepository.findOne(paymentGateway.getId());
				    	gateway.setObsId(result.resourceId());
				    	gateway.setStatus("Success");    	
				    	 this.paymentGatewayRepository.save(gateway);
				    	
				    }else{
				    	PaymentGateway gateway=this.paymentGatewayRepository.findOne(paymentGateway.getId());
				    	gateway.setStatus("Failure");    	
				    	this.paymentGatewayRepository.save(gateway);
				    }
	                 
					return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paymentGateway.getId()).build();
				   }else{
					   return new CommandProcessingResult(Long.valueOf(-1));
				   }
				   
			} catch (DataIntegrityViolationException dve) {
	           // return CommandProcessingResult.empty();
				    handleCodeDataIntegrityIssues(element, dve);
					return new CommandProcessingResult(Long.valueOf(-1));
	        } catch (IllegalAccessException e) {	 
	        	  final String name = fromApiJsonHelper.extractStringNamed("KEY_ID", element);
		          throw new PlatformDataIntegrityException("error.msg.code.BillerRef", "A BillerRef with this value '" + name + "' does not exists");			
			} 
			
		}
		
		private void handleCodeDataIntegrityIssues(JsonElement element,
				DataIntegrityViolationException dve) {
			
			 Throwable realCause = dve.getMostSpecificCause();
		        if (realCause.getMessage().contains("KEY_ID")) {
		            final String name = fromApiJsonHelper.extractStringNamed("KEY_ID", element);
		            throw new PlatformDataIntegrityException("error.msg.code.BillerRef", "A BillerRef with this value '" + name + "' does not exists");
		        }
		        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
		                "Unknown data integrity issue with resource: " + realCause.getMessage());
			
		}
			 
	    	

	
	
}

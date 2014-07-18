package org.mifosplatform.finance.paymentsgateway.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.finance.payments.service.PaymentWritePlatformService;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.finance.paymentsgateway.serialization.PaymentGatewayCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	    private final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService;
	   
	    @Autowired
	    public PaymentGatewayWritePlatformServiceImpl(final PlatformSecurityContext context,
	    	    final PaymentGatewayRepository paymentGatewayRepository,final FromJsonHelper fromApiJsonHelper,
	    		final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer,
	    		final PaymentGatewayReadPlatformService readPlatformService,
	    		final PaymentWritePlatformService paymentWritePlatformService,
	    		final PaymodeReadPlatformService paymodeReadPlatformService,
	    		final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService)
	    {
	    	this.context=context;
	    	this.paymentGatewayRepository=paymentGatewayRepository;
	    	this.fromApiJsonHelper=fromApiJsonHelper;
	    	this.paymentGatewayCommandFromApiJsonDeserializer=paymentGatewayCommandFromApiJsonDeserializer;
	    	this.readPlatformService=readPlatformService;
	    	this.paymentWritePlatformService=paymentWritePlatformService;
	    	this.paymodeReadPlatformService=paymodeReadPlatformService;
	    	this.paymentGatewayReadPlatformService=paymentGatewayReadPlatformService;
	    }
	    
	    private Long MPesaTransaction(JsonElement element) {

			try {
				CommandProcessingResult result = null;
				String serialNumberId = fromApiJsonHelper.extractStringNamed("reference", element);
				String paymentDate = fromApiJsonHelper.extractStringNamed("timestamp", element);
				BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
				String phoneNo = fromApiJsonHelper.extractStringNamed("msisdn",element);
				String receiptNo = fromApiJsonHelper.extractStringNamed("receipt",element);
				String SOURCE = fromApiJsonHelper.extractStringNamed("service",element);
				String details = fromApiJsonHelper.extractStringNamed("name",element);
				DateFormat readFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
				Date date;

				date = readFormat.parse(paymentDate);

				PaymentGateway paymentGateway = new PaymentGateway(serialNumberId,phoneNo, date, amountPaid, receiptNo, SOURCE, details);

				Long clientId = this.readPlatformService.retrieveClientIdForProvisioning(serialNumberId);

				if (clientId != null && clientId>0) {
		
					Long paymodeId = this.paymodeReadPlatformService.getOnlinePaymode();
					if (paymodeId == null) {
						paymodeId = new Long(83);
					}
					String remarks = "customerName: " + details + " ,PhoneNo:"+ phoneNo + " ,Biller account Name : " + SOURCE;
					SimpleDateFormat daformat = new SimpleDateFormat("dd MMMM yyyy");
					String paymentdate = daformat.format(date);
					JsonObject object = new JsonObject();
					object.addProperty("dateFormat", "dd MMMM yyyy");
					object.addProperty("locale", "en");
					object.addProperty("paymentDate", paymentdate);
					object.addProperty("amountPaid", amountPaid);
					object.addProperty("isChequeSelected", "no");
					object.addProperty("receiptNo", receiptNo);
					object.addProperty("remarks", remarks);
					object.addProperty("paymentCode", paymodeId);
					String entityName = "PAYMENT";
					final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
					JsonCommand comm = new JsonCommand(null, object.toString(),element1, fromApiJsonHelper,entityName,
							                            clientId,null, null, null, null, null, null, null, null, null,null);
					
					result = this.paymentWritePlatformService.createPayment(comm);
					if (result.resourceId() != null) {
						paymentGateway.setObsId(result.resourceId());
						paymentGateway.setStatus("Success");
						paymentGateway.setAuto(false);
						this.paymentGatewayRepository.save(paymentGateway);
					}
					return result.resourceId();
				} else {
					paymentGateway.setStatus("Failure");
					this.paymentGatewayRepository.save(paymentGateway);
					return null;
				}
			} catch (ParseException e) {
				 return Long.valueOf(-1);
			}

		}
	    
	    private Long TigoPesaTransaction(JsonElement element) {
	    	CommandProcessingResult result = null;
			
			String serialNumberId = fromApiJsonHelper.extractStringNamed("CUSTOMERREFERENCEID", element);
			String TXNID = fromApiJsonHelper.extractStringNamed("TXNID", element);
			BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("AMOUNT", element);
			String phoneNo = fromApiJsonHelper.extractStringNamed("MSISDN", element);
			String TYPE = fromApiJsonHelper.extractStringNamed("TYPE", element);
			String tStatus = fromApiJsonHelper.extractStringNamed("STATUS", element);
			String details = fromApiJsonHelper.extractStringNamed("COMPANYNAME", element);		 
			Date date = new Date();		
			String SOURCE="tigo";

			PaymentGateway paymentGateway = new PaymentGateway(serialNumberId,TXNID,amountPaid,phoneNo, TYPE, tStatus, details, date, SOURCE);

			Long clientId = this.readPlatformService.retrieveClientIdForProvisioning(serialNumberId);

			if (clientId != null && clientId>0) {
				Long paymodeId = this.paymodeReadPlatformService.getOnlinePaymode();
				if (paymodeId == null) {
					paymodeId = new Long(83);
				}
				String remarks = "companyName: " + details + " ,PhoneNo:"+ phoneNo + " ,Biller account Name : " + SOURCE + 
						       " ,Type:"+TYPE+ " ,Status:"+tStatus;
				
				SimpleDateFormat daformat = new SimpleDateFormat("dd MMMM yyyy");
				String paymentdate = daformat.format(date);
				JsonObject object = new JsonObject();
				object.addProperty("dateFormat", "dd MMMM yyyy");
				object.addProperty("locale", "en");
				object.addProperty("paymentDate", paymentdate);
				object.addProperty("amountPaid", amountPaid);
				object.addProperty("isChequeSelected", "no");
				object.addProperty("receiptNo", TXNID);
				object.addProperty("remarks", remarks);
				object.addProperty("paymentCode", paymodeId);
				String entityName = "PAYMENT";
				final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
				JsonCommand comm = new JsonCommand(null, object.toString(),element1, fromApiJsonHelper,entityName,
						                            clientId,null, null, null, null, null, null, null, null, null,null);
				
				result = this.paymentWritePlatformService.createPayment(comm);
				if (result.resourceId() != null) {
					paymentGateway.setObsId(result.resourceId());
					paymentGateway.setStatus("Success");
					paymentGateway.setAuto(false);
					this.paymentGatewayRepository.save(paymentGateway);
				}
				return result.resourceId();
			} else {
				paymentGateway.setStatus("Failure");
				this.paymentGatewayRepository.save(paymentGateway);
				return null;
			}

		}

	    @Transactional
		@Override
		public CommandProcessingResult createPaymentGateway(JsonCommand command) {
			  JsonElement element=null;
			  Long resourceId = null;
			  String OBSPAYMENTTYPE = null;
			  element= fromApiJsonHelper.parse(command.json());
			try {
				   context.authenticatedUser();
				   this.paymentGatewayCommandFromApiJsonDeserializer.validateForCreate(command.json());
				  

				   if(element!=null){  
					   OBSPAYMENTTYPE  = fromApiJsonHelper.extractStringNamed("OBSPAYMENTTYPE", element);
					   if(OBSPAYMENTTYPE.equalsIgnoreCase("MPesa")){
						   resourceId = this.MPesaTransaction(element);
					   }else if (OBSPAYMENTTYPE.equalsIgnoreCase("TigoPesa")) {
						   resourceId= this.TigoPesaTransaction(element);
					   }  
				   }	 
				   return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(resourceId).build();
			}catch (DataIntegrityViolationException  e) {

	    	  if(e.toString().contains("receipt_no")){
		    	  final String receiptNo=fromApiJsonHelper.extractStringNamed("receipt", element);	    	     	 
		    	  throw new ReceiptNoDuplicateException(receiptNo);	    	  	    	  
	    	  }else{
	    		  return null;

	    	  }
		   }catch (ReceiptNoDuplicateException  e) {
				   String receiptNo = null;
				   if(OBSPAYMENTTYPE.equalsIgnoreCase("MPesa")){
					   receiptNo =fromApiJsonHelper.extractStringNamed("receipt", element);
				   }else if (OBSPAYMENTTYPE.equalsIgnoreCase("TigoPesa")) {
					   receiptNo=fromApiJsonHelper.extractStringNamed("TXNID", element);
				   } 
		    	  String receiptNO=this.paymentGatewayReadPlatformService.findReceiptNo(receiptNo);
		    	  if(receiptNO!=null){
		    	  throw new ReceiptNoDuplicateException(receiptNo);
		    	  }
		    	  else{
		    		   return null;
		    	  }
			   } catch (Exception dve) {
				    handleCodeDataIntegrityIssues(command, dve);
					return new CommandProcessingResult(Long.valueOf(-1));
	        }		
			
		}

		private void handleCodeDataIntegrityIssues(JsonCommand command,Exception dve) {
			String realCause=dve.toString();
			  final String receiptNo=command.stringValueOfParameterNamed("receipt");//fromApiJsonHelper.extractStringNamed("receipt", command);
		        if (realCause.contains("reference")) {
		        	
		            final String name =command.stringValueOfParameterNamed("reference");// fromApiJsonHelper.extractStringNamed("reference", command);
		            throw new PlatformDataIntegrityException("error.msg.code.reference", "A reference with this value '" + name + "' does not exists");
		        }else if(realCause.contains("receiptNo")){
		        	
		        	throw new PlatformDataIntegrityException("error.msg.payments.duplicate.receiptNo", "A code with receiptNo'"
		                    + receiptNo + "'already exists", "displayName",receiptNo);
		        	
		        }
		        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
		                "Unknown data integrity issue with resource: " + realCause);
			
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

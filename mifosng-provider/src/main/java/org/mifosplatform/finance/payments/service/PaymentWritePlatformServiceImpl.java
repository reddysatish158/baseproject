
package org.mifosplatform.finance.payments.service;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.domain.InvoiceRepository;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.finance.clientbalance.service.UpdateClientBalance;
import org.mifosplatform.finance.payments.domain.ChequePayment;
import org.mifosplatform.finance.payments.domain.ChequePaymentRepository;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.domain.PaymentRepository;
import org.mifosplatform.finance.payments.domain.PaypalEnquirey;
import org.mifosplatform.finance.payments.domain.PaypalEnquireyRepository;
import org.mifosplatform.finance.payments.exception.PaymentDetailsNotFoundException;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.finance.payments.serialization.PaymentCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;

@Service
public class PaymentWritePlatformServiceImpl implements PaymentWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(PaymentWritePlatformServiceImpl.class);
    private final String Paypal_method="paypal";
    private final String CreditCard_method="credit_card";
    private final String CreditCard="creditCard";
    private final String CreditCardToken="creditCardToken";
	
	private final PlatformSecurityContext context;
	private final PaymentRepository paymentRepository;
	private final PaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ClientBalanceReadPlatformService clientBalanceReadPlatformService;
	private final ClientBalanceRepository clientBalanceRepository;
	private final UpdateClientBalance updateClientBalance;
	private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private final ChequePaymentRepository chequePaymentRepository;
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService; 
	private final PaymodeReadPlatformService paymodeReadPlatformService ;
	private final InvoiceRepository invoiceRepository;
	private final GlobalConfigurationRepository globalConfigurationRepository;
	private final PaypalEnquireyRepository paypalEnquireyRepository;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public PaymentWritePlatformServiceImpl(final PlatformSecurityContext context,final PaymentRepository paymentRepository,
			final PaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ClientBalanceReadPlatformService clientBalanceReadPlatformService,
			final ClientBalanceRepository clientBalanceRepository,final ChequePaymentRepository chequePaymentRepository,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final UpdateClientBalance updateClientBalance,final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final PaymodeReadPlatformService paymodeReadPlatformService,
			final InvoiceRepository invoiceRepository,final GlobalConfigurationRepository globalConfigurationRepository,
			final PaypalEnquireyRepository paypalEnquireyRepository,final FromJsonHelper fromApiJsonHelper) {
		this.context = context;
		this.paymentRepository = paymentRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.clientBalanceReadPlatformService=clientBalanceReadPlatformService;
		this.clientBalanceRepository=clientBalanceRepository;
		this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
		this.updateClientBalance= updateClientBalance;
		this.chequePaymentRepository=chequePaymentRepository;
		this.actiondetailsWritePlatformService=actiondetailsWritePlatformService; 
		this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		this.paymodeReadPlatformService=paymodeReadPlatformService;
		this.invoiceRepository=invoiceRepository;
		this.globalConfigurationRepository=globalConfigurationRepository;
		this.paypalEnquireyRepository=paypalEnquireyRepository;
		this.fromApiJsonHelper=fromApiJsonHelper;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createPayment(JsonCommand command) {
		try {
			context.authenticatedUser();

			this.fromApiJsonDeserializer.validateForCreate(command.json());
			List<ClientBalanceData> clientBalancedatas = clientBalanceReadPlatformService.retrieveAllClientBalances(command.entityId());
			
			Long id=Long.valueOf(-1);
			if(clientBalancedatas.size() == 1)
				id= createPayments(clientBalancedatas.get(0).getId(),command.entityId(),command);											
			else
			id=	createPayments(command.entityId(),command.entityId(),command);
			
			if(command.stringValueOfParameterNamed("isChequeSelected").equalsIgnoreCase("yes")){
				ChequePayment chequePayment = ChequePayment.fromJson(command);
				chequePayment.setPaymentId(id);
				chequePaymentRepository.save(chequePayment);
			}
			
			//Add New Action 
			List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_CREATE_PAYMENT);
			if(actionDetaislDatas.size() != 0){
			this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,command.entityId(), id.toString());
			}
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(id).build();
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		}
	}
	
	@Transactional
	@Override
	public Long createPayments(Long clientBalanceid, Long clientid,JsonCommand command) {
                                                                              
		try {
			this.context.authenticatedUser();
			Payment payment = null;
			payment  = Payment.fromJson(command,clientid);
			this.paymentRepository.saveAndFlush(payment);

			Long getPayMode = Long.parseLong(String.valueOf(payment.getPaymodeCode()));
			
			McodeData payModeData=paymodeReadPlatformService.retrieveSinglePaymode(getPayMode);
		
			/* Manoj code for updating client balance */
              
			ClientBalance clientBalance = null;
			if(clientBalanceid!=null)
			clientBalance = clientBalanceRepository.findOne(clientBalanceid);

			if(clientBalance != null){
				clientBalance = updateClientBalance.doUpdatePaymentClientBalance(clientid,command,clientBalance);

			}else if(clientBalance == null){

				clientBalance = updateClientBalance.createPaymentClientBalance(clientid,command, clientBalance);
			}

			updateClientBalance.saveClientBalanceEntity(clientBalance);
			
			//Update Invoice Amount
			if(payment.getInvoiceId() != null){
				
				Invoice invoice=this.invoiceRepository.findOne(payment.getInvoiceId());
				invoice.updateAmount(payment.getAmountPaid());
				this.invoiceRepository.save(invoice);
				
			}
		
			transactionHistoryWritePlatformService.saveTransactionHistory(payment.getClientId(), "PAYMENT", payment.getPaymentDate(),
					"AmountPaid:"+payment.getAmountPaid(),"PayMode:"+payModeData.getPaymodeCode(),"Remarks:"+payment.getRemarks(),
					"ReceiptNo: "+payment.getReceiptNo());
			return payment.getId();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return Long.valueOf(-1);
		}
	}

	@Override
	public CommandProcessingResult cancelPayment(JsonCommand command,Long paymentId) {
		
		try{
			this.fromApiJsonDeserializer.validateForCancel(command.json());
			Payment payment=this.paymentRepository.findOne(paymentId);
			if(payment == null){
				throw new PaymentDetailsNotFoundException(paymentId.toString());
			}
			
			payment.cancelPayment(command);
			this.paymentRepository.save(payment);
			ClientBalance clientBalance = clientBalanceRepository.findByClientId(payment.getClientId());
			clientBalance.setBalanceAmount(clientBalance.getBalanceAmount().add(payment.getAmountPaid()));
			this.clientBalanceRepository.save(clientBalance);
			
			transactionHistoryWritePlatformService.saveTransactionHistory(payment.getClientId(), "Cancel Payment", payment.getPaymentDate(),
					"Amount :"+payment.getAmountPaid(),"Remarks:"+payment.getCancelRemark(),"ReceiptNo: "+payment.getReceiptNo());
			return new CommandProcessingResult(paymentId);
			
			
		}catch(DataIntegrityViolationException exception){
			return CommandProcessingResult.empty();
		}
		
	}

	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
		Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("receipt_no")){
		          throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
			/* throw new PlatformDataIntegrityException("error.msg.payments.duplicate.receiptNo", "A code with receiptNo'"
	                    + command.stringValueOfParameterNamed("receiptNo") + "'already exists", "displayName", command.stringValueOfParameterNamed("receiptNo"));*/
		}
		
		logger.error(dve.getMessage(), dve);
	}
	
	private void DataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve, String paymentid) {
		Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("payment_id")){
		 throw new PlatformDataIntegrityException("error.msg.payments.paypalEnquirey.duplicate.paymentId", "A code with paymentId '"
                  + paymentid + "' already exists", "displayName", paymentid);
	}
		
		logger.error(dve.getMessage(), dve);
	}

	@Transactional
	@Override
	public CommandProcessingResult paypalEnquirey(JsonCommand command) {

		try{
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForpaypalEnquirey(command.json());
					
			JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
			SimpleDateFormat pattern= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");	
			
			String state=obj.getString("state");
			String paymentid=obj.getString("id");
			String create_time=obj.getString("create_time");
			Date date= pattern.parse(create_time);
			
			PaypalEnquirey  paypalEnquirey = new PaypalEnquirey(command.entityId(),state,paymentid,date);			
			this.paypalEnquireyRepository.save(paypalEnquirey);
			
			JsonObject paymentobject = new JsonObject();
			Map<String, Object> changes = new HashMap<String, Object>();
			ClientBalance clientBalance = clientBalanceRepository.findByClientId(paypalEnquirey.getClientId());
			
			PaypalEnquirey paypalEnquireyUpdate = this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
			
			try{
				
				 SendingDataToPaypal(paypalEnquirey.getId());
				 
			}catch (PayPalRESTException e) {
				
				PaypalEnquirey paypalexceptionupdate=this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
				paypalexceptionupdate.setDescription(e.getLocalizedMessage());
				paypalexceptionupdate.setStatus("Fail");
				this.paypalEnquireyRepository.save(paypalexceptionupdate);
				changes.put("paymentId", new Long(-1));
				changes.put("paymentStatus", "Fail");
				changes.put("totalBalance", clientBalance.getBalanceAmount());
				changes.put("paypalException", e.getMessage());
				return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
			} 
			 
			    String remarks=paypalEnquireyUpdate.getPayerEmailId()!=null?paypalEnquireyUpdate.getPayerEmailId():" "+
			                          paypalEnquireyUpdate.getCardNumber()!=null?paypalEnquireyUpdate.getCardNumber():" ";
				
				String paymentdate = new SimpleDateFormat("dd MMMM yyyy").format(paypalEnquireyUpdate.getPaymentDate());
				
				paymentobject.addProperty("txn_id", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("dateFormat", "dd MMMM yyyy");
				paymentobject.addProperty("locale", "en");
				paymentobject.addProperty("paymentDate", paymentdate);
				paymentobject.addProperty("amountPaid", paypalEnquireyUpdate.getTotalAmount());
				paymentobject.addProperty("isChequeSelected", "no");
				paymentobject.addProperty("receiptNo", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("remarks", remarks);
				paymentobject.addProperty("paymentCode", 27);
				String entityName = "PAYMENT";
				
				final JsonElement element1 = fromApiJsonHelper.parse(paymentobject.toString());
				Long id = null;
				
				JsonCommand comm = new JsonCommand(null,paymentobject.toString(), element1,fromApiJsonHelper, entityName, 
						paypalEnquirey.getClientId(),null, null, null, null, null, null, null, null,null,null);

				CommandProcessingResult result = createPayment(comm);

				if (result.resourceId() != null) {
					int i = new Long(0).compareTo(result.resourceId());
					if (i == -1) {
						paypalEnquireyUpdate.setStatus("Success");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Success");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "");

					} else {
						paypalEnquireyUpdate.setStatus("Fail");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Fail");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "Payment Error");
					}
				} else {
					paypalEnquireyUpdate.setStatus("Failure");
					changes.put("paymentId", "");
					changes.put("paymentStatus", "Failure");
					changes.put("totalBalance",clientBalance.getBalanceAmount());
					changes.put("paypalException", "");
					changes.put("Errordescription", "Payment Not Processed");
				}
				
				this.paypalEnquireyRepository.save(paypalEnquireyUpdate);
			
	        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
	        
		} catch (ParseException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (JSONException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (IOException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (DataIntegrityViolationException dve){
			
			try {
				JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
				String paymentid=obj.getString("id");
				DataIntegrityIssues(command, dve, paymentid);
				return CommandProcessingResult.empty();
			} catch (JSONException e) {
				return CommandProcessingResult.empty();
			}
			
		}
		
		
	}

	private void SendingDataToPaypal(Long paypalEnquireyId) throws PayPalRESTException, IOException, JSONException {
		
		   try {
				Properties prop = new Properties();
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sdk_config.properties");
				prop.load(is);
				PaypalEnquirey paypalEnquirey = this.paypalEnquireyRepository.findOne(paypalEnquireyId);
				com.paypal.api.payments.Payment.initConfig(prop);
				GlobalConfigurationProperty paypalGlobalData = this.globalConfigurationRepository.findOneByName("Is_Paypal");
				JSONObject object = new JSONObject(paypalGlobalData.getValue());
				String paypalClientId = object.getString("clientId");
				String paypalsecretCode = object.getString("secretCode");
			
				OAuthTokenCredential tokenCredential = new OAuthTokenCredential(paypalClientId, paypalsecretCode);
				com.paypal.api.payments.Payment payment = com.paypal.api.payments.Payment
						.get(tokenCredential.getAccessToken().trim(),paypalEnquirey.getPaymentId());

				String paymentMethod = payment.getPayer().getPaymentMethod();
				
				if (paymentMethod.equalsIgnoreCase(Paypal_method)) {

					String EmailId = payment.getPayer().getPayerInfo().getEmail();
					String PayerId = payment.getPayer().getPayerInfo().getPayerId();
					String amount = payment.getTransactions().get(0).getAmount().getTotal();
					String currency = payment.getTransactions().get(0).getAmount().getCurrency();
					String description = payment.getTransactions().get(0).getDescription();
					String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
					BigDecimal totalAmount = new BigDecimal(amount);

					paypalEnquirey.fromPaypalEnquireyTransaction(EmailId,PayerId, totalAmount, currency, description, paymentState, paymentMethod);		
					 
				}else if(paymentMethod.equalsIgnoreCase(CreditCard_method)){	
					JSONObject obj=new JSONObject(payment.getPayer().getFundingInstruments().get(0));
					
					if(obj.has(CreditCard)){
						String cardNumber = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getNumber();
						String cardType = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getType();
						int cardExpiryMonth = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getExpireMonth();
						int cardExpiryYear = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getExpireYear();
						String amount = payment.getTransactions().get(0).getAmount().getTotal();
						String currency = payment.getTransactions().get(0).getAmount().getCurrency();
						String description = payment.getTransactions().get(0).getDescription();
						String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
						BigDecimal totalAmount = new BigDecimal(amount);
						String cardExpiryDate=Integer.toString(cardExpiryMonth)+"/"+Integer.toString(cardExpiryYear);

						paypalEnquirey.fromPaypalEnquireyTransaction(cardNumber, cardType, cardExpiryDate, totalAmount, currency, description,paymentState, paymentMethod);	
					
					}else if(obj.has(CreditCardToken)){
						
						String cardNumber = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getLast4();
						String cardType = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getType();
						int cardExpiryMonth = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getExpireMonth();
						int cardExpiryYear = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getExpireYear();
						String amount = payment.getTransactions().get(0).getAmount().getTotal();
						String currency = payment.getTransactions().get(0).getAmount().getCurrency();
						String description = payment.getTransactions().get(0).getDescription();
						String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
						BigDecimal totalAmount = new BigDecimal(amount);
						String cardExpiryDate=Integer.toString(cardExpiryMonth)+"/"+Integer.toString(cardExpiryYear);
						paypalEnquirey.fromPaypalEnquireyTransaction(cardNumber, cardType, cardExpiryDate, totalAmount, currency, description,paymentState, paymentMethod);	
					}else{
						return;
					}
					
				}
				
				this.paypalEnquireyRepository.save(paypalEnquirey);
				
				
			} catch (PayPalRESTException e) {	
				
				throw new PayPalRESTException(e.getMessage());		
				
			} catch (IOException e) {
				
				throw new IOException(e.getMessage());		
				
			} catch (JSONException e) {
				
				throw new JSONException(e.getMessage());	
			}
		
		
	}

}




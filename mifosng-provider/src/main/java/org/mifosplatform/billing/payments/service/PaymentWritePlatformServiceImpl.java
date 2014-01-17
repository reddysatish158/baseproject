package org.mifosplatform.billing.payments.service;
import java.util.List;

import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.service.ActionDetailsReadPlatformService;
import org.mifosplatform.billing.action.service.ActiondetailsWritePlatformService;
import org.mifosplatform.billing.action.service.EventActionConstants;
import org.mifosplatform.billing.clientbalance.data.ClientBalanceData;
import org.mifosplatform.billing.clientbalance.domain.ClientBalance;
import org.mifosplatform.billing.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.billing.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.billing.clientbalance.service.UpdateClientBalance;
import org.mifosplatform.billing.payments.domain.ChequePayment;
import org.mifosplatform.billing.payments.domain.ChequePaymentRepository;
import org.mifosplatform.billing.payments.domain.Payment;
import org.mifosplatform.billing.payments.domain.PaymentRepository;
import org.mifosplatform.billing.payments.exception.PaymentDetailsNotFoundException;
import org.mifosplatform.billing.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.billing.payments.serialization.PaymentCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWritePlatformServiceImpl implements PaymentWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(PaymentWritePlatformServiceImpl.class);

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
	

	@Autowired
	public PaymentWritePlatformServiceImpl(final PlatformSecurityContext context,final PaymentRepository paymentRepository,
			final PaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ClientBalanceReadPlatformService clientBalanceReadPlatformService,
			final ClientBalanceRepository clientBalanceRepository,final ChequePaymentRepository chequePaymentRepository,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final UpdateClientBalance updateClientBalance,final ActiondetailsWritePlatformService actiondetailsWritePlatformService) {
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
		
	}

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
	

	@SuppressWarnings("unused")
	@Transactional
	@Override
	public Long createPayments(Long clientBalanceid, Long clientid,JsonCommand command) {
                                                                              
		try {
			this.context.authenticatedUser();
			Payment payment = null;
			payment  = Payment.fromJson(command,clientid);
			this.paymentRepository.saveAndFlush(payment);


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
			
			//Perform Event Action
		//	this.actiondetailsWritePlatformService.AddNewActions(clientid,payment.getId());
			
			transactionHistoryWritePlatformService.saveTransactionHistory(payment.getClientId(), "Payment", payment.getPaymentDate(),"AmountPaid:"+payment.getAmountPaid(),"PayMode:"+payment.getPaymodeCode(),"Remarks:"+payment.getRemarks(),"PaymentID:"+payment.getId(),"ReceiptNo: "+payment.getReceiptNo());
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
			return new CommandProcessingResult(paymentId);
			
			
		}catch(DataIntegrityViolationException exception){
			return CommandProcessingResult.empty();
		}
		
	}

	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve){
		Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("receipt_no")){
		          throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
			/* throw new PlatformDataIntegrityException("error.msg.payments.duplicate.receiptNo", "A code with receiptNo'"
	                    + command.stringValueOfParameterNamed("receiptNo") + "'already exists", "displayName", command.stringValueOfParameterNamed("receiptNo"));*/
		}
		
		logger.error(dve.getMessage(), dve);
	}
}



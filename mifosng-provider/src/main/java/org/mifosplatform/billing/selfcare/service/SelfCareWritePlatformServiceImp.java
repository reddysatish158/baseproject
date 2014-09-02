package org.mifosplatform.billing.selfcare.service;

import java.util.Date;

import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.billing.loginhistory.exception.LoginHistoryNotFoundException;
import org.mifosplatform.billing.selfcare.data.SelfCareData;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.EmailDetail;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.PlatformEmailService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.mifosplatform.logistics.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.client.exception.ClientStatusException;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;


@Service
public class SelfCareWritePlatformServiceImp implements SelfCareWritePlatformService{
	
	private PlatformSecurityContext context;
	private SelfCareRepository selfCareRepository;
	private FromJsonHelper fromJsonHelper;
	private SelfCareCommandFromApiJsonDeserializer selfCareCommandFromApiJsonDeserializer;
	private SelfCareReadPlatformService selfCareReadPlatformService;
	private PlatformEmailService platformEmailService; 
	private TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private MessagePlatformEmailService messagePlatformEmailService;
	private ClientRepository clientRepository;
	private final OwnedHardwareJpaRepository ownedHardwareJpaRepository;
	private final BillingMessageTemplateRepository billingMessageTemplateRepository;
		
	private final static Logger logger = (Logger) LoggerFactory.getLogger(SelfCareWritePlatformServiceImp.class);
	
	@Autowired
	public SelfCareWritePlatformServiceImp(final PlatformSecurityContext context, final SelfCareRepository selfCareRepository, 
			final FromJsonHelper fromJsonHelper, final SelfCareCommandFromApiJsonDeserializer selfCareCommandFromApiJsonDeserializer, 
			final SelfCareReadPlatformService selfCareReadPlatformService, final PlatformEmailService platformEmailService, 
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final MessagePlatformEmailService messagePlatformEmailService,
			ClientRepository clientRepository,final OwnedHardwareJpaRepository ownedHardwareJpaRepository,
			final BillingMessageTemplateRepository billingMessageTemplateRepository) {
		this.context = context;
		this.selfCareRepository = selfCareRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.selfCareCommandFromApiJsonDeserializer = selfCareCommandFromApiJsonDeserializer;
		this.selfCareReadPlatformService = selfCareReadPlatformService;
		this.platformEmailService = platformEmailService;
		this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
		this.messagePlatformEmailService= messagePlatformEmailService;
		this.clientRepository=clientRepository;
		this.ownedHardwareJpaRepository=ownedHardwareJpaRepository;
		this.billingMessageTemplateRepository=billingMessageTemplateRepository;
				
	}
	
	@Override
	public CommandProcessingResult createSelfCare(JsonCommand command) {
		SelfCare selfCare = null;
		Long clientId = null;
		String email = null;
		try{
			context.authenticatedUser();
			selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			selfCare = SelfCare.fromJson(command);
			try{
			clientId = selfCareReadPlatformService.getClientId(selfCare.getUniqueReference());
			email = selfCareReadPlatformService.getEmail(clientId);
			}catch(EmptyResultDataAccessException erdae){
				throw new PlatformDataIntegrityException("invalid.account.details","invalid.account.details");
			}
			
			if(clientId !=null && clientId > 0 ){
				selfCare.setClientId(clientId);
				RandomPasswordGenerator passwordGenerator = new RandomPasswordGenerator(8);
				String unencodedPassword = passwordGenerator.generate();
				selfCare.setPassword(unencodedPassword);
				selfCareRepository.save(selfCare);
				Client client= this.clientRepository.findOne(clientId);
				BillingMessageTemplate messageDetails=this.billingMessageTemplateRepository.findByTemplateDescription("SELF CARE");
				String subject=messageDetails.getSubject();
				String body=messageDetails.getBody();
				String header=messageDetails.getHeader().replace("PARAM1", client.getDisplayName()+","+"\n");
				body=body.replace("PARAM2"," "+email );
				body=body.replace("PARAM3",messageDetails.getFooter());
				body=body.replace("PARAM4",selfCare.getUserName());
				body=body.replace("PARAM5", unencodedPassword);
				StringBuilder body1 =new StringBuilder(header).append(body+"\n").append("\n"+"Thanks"+"\n"+messageDetails.getFooter());
				body=new String(body1);
				messagePlatformEmailService.sendGeneralMessage(email, body, subject);
				/*platformEmailService.sendToUserAccount(new EmailDetail("Hugo Self Care Organisation ", "SelfCare",email, selfCare.getUserName()), unencodedPassword);*/
				
				transactionHistoryWritePlatformService.saveTransactionHistory(clientId, "Self Care user activation", new Date(), "USerName: "+selfCare.getUserName()+" ClientId"+selfCare.getClientId());
			}else{
				throw new PlatformDataIntegrityException("client does not exist", "client not registered","clientId", "client is null ");
			}
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.username", "duplicate.username","duplicate.username", "duplicate.username");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
		return new CommandProcessingResultBuilder().withEntityId(selfCare.getClientId()).build();
	}
	
	
	
	@Override
	public CommandProcessingResult createSelfCareUDPassword(JsonCommand command) {
		SelfCare selfCare = null;
		Long clientId = null;
		try{
			context.authenticatedUser();
			selfCareCommandFromApiJsonDeserializer.validateForCreateUDPassword(command);
			selfCare = SelfCare.fromJsonODP(command);
			try{
			clientId = selfCareReadPlatformService.getClientId(selfCare.getUniqueReference());
			if(clientId == null || clientId <= 0 ){
				throw new PlatformDataIntegrityException("client does not exist", "this user is not registered","clientId", "client is null ");
			}
			selfCare.setClientId(clientId);
			selfCareRepository.save(selfCare);
			transactionHistoryWritePlatformService.saveTransactionHistory(clientId, "Self Care user activation", new Date(), "USerName: "+selfCare.getUserName()+" ClientId"+selfCare.getClientId());
			}
			catch(EmptyResultDataAccessException dve){
				throw new PlatformDataIntegrityException("invalid.account.details","invalid.account.details","this user is not registered");
			}
			
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.email", "duplicate.email","duplicate.email", "duplicate.email");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
		return new CommandProcessingResultBuilder().withEntityId(selfCare.getClientId()).build();
	}
	@Override
	public CommandProcessingResult updateSelfCareUDPassword(JsonCommand command) {
		   SelfCare selfCare=null;
		   context.authenticatedUser();
		   selfCareCommandFromApiJsonDeserializer.validateForUpdateUDPassword(command);
		   String email=command.stringValueOfParameterNamed("uniqueReference");
		   String password=command.stringValueOfParameterNamed("password");
		   selfCare=this.selfCareRepository.findOneByEmail(email);
		   if(selfCare==null){
			   throw new ClientNotFoundException(email);
		   }
		   selfCare.setPassword(password);
		   this.selfCareRepository.save(selfCare);
		   return new CommandProcessingResultBuilder().withEntityId(selfCare.getClientId()).build();
	}
	@Override
	public CommandProcessingResult forgotSelfCareUDPassword(JsonCommand command) {
		SelfCare selfCare=null;
		context.authenticatedUser();
		selfCareCommandFromApiJsonDeserializer.validateForForgotUDPassword(command);
		String email=command.stringValueOfParameterNamed("uniqueReference");
		selfCare=this.selfCareRepository.findOneByEmail(email);
		if(selfCare == null){
			throw new ClientNotFoundException(email);
		}
		String password=selfCare.getPassword();
		Client client= this.clientRepository.findOne(selfCare.getClientId());
		String body="Dear "+client.getDisplayName()+","+"\n"+"Your login information is mentioned below."+"\n"+"Email Id : "+email+"\n"+"Password :"+password+"\n"+"Thanks";
		String subject="Login Information";
		messagePlatformEmailService.sendGeneralMessage(email, body, subject);
		return new CommandProcessingResult(selfCare.getClientId());
	}
   
	private void handleDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
		 logger.error(dve.getMessage(), dve);
	        if (realCause.getMessage().contains("username")){	
	        	throw new PlatformDataIntegrityException("validation.error.msg.selfcare.duplicate.userName", "User Name: " + command.stringValueOfParameterNamed("userName")+ " already exists", "userName", command.stringValueOfParameterNamed("userName"));
	        }else if (realCause.getMessage().contains("unique_reference")){
	        	throw new PlatformDataIntegrityException("validation.error.msg.selfcare.duplicate.email", "email: " + command.stringValueOfParameterNamed("uniqueReference")+ " already exists", "email", command.stringValueOfParameterNamed("uniqueReference"));
	        }
		
	}
	
	@Override
	public CommandProcessingResult updateClientStatus(JsonCommand command,Long entityId) {
            try{
            	
            	this.context.authenticatedUser();
            	String status=command.stringValueOfParameterNamed("status");
            	SelfCare client=this.selfCareRepository.findOneByClientId(entityId);
            	if(client == null){
            		throw new ClientNotFoundException(entityId);
            	}
            	if(status.equalsIgnoreCase("ACTIVE")){
            	
            		if(status.equals(client.getStatus())){
            			throw new ClientStatusException(entityId);
            		}
            	}
            	client.setStatus(status);
            	this.selfCareRepository.save(client);
            	return new CommandProcessingResult(Long.valueOf(entityId));
            	
            }catch(DataIntegrityViolationException dve){
            	handleDataIntegrityIssues(command, dve);
            	return new CommandProcessingResult(Long.valueOf(-1));
            }

		
	}

	/*@Override
	public void verifyActiveViewers(String serialNo, Long clientId) {
	   		
       	OwnedHardware ownedHardware =this.ownedHardwareJpaRepository.findBySerialNumber(serialNo, clientId);
       	ownedHardware.setStatus("ACTIVE");
       	this.ownedHardwareJpaRepository.saveAndFlush(ownedHardware);
       	
       }*/

	
}
package org.mifosplatform.organisation.redemption.service;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mifosplatform.finance.adjustment.service.AdjustmentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGenerator;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetails;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetailsRepository;
import org.mifosplatform.organisation.redemption.exception.PinNumberNotFoundException;
import org.mifosplatform.organisation.redemption.serialization.RedemptionCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class RedemptionWritePlatformServiceImpl implements
		RedemptionWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(RedemptionWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final RandomGeneratorDetailsRepository randomGeneratorDetailsRepository;
	private final ClientRepository clientRepository;
	private final AdjustmentWritePlatformService adjustmentWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	private final RedemptionReadPlatformService redemptionReadPlatformService;
	private final RedemptionCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final OrderRepository orderRepository;
	
	@Autowired
	public RedemptionWritePlatformServiceImpl(final PlatformSecurityContext context,final RandomGeneratorDetailsRepository randomGeneratorDetailsRepository,
		final ClientRepository clientRepository,final AdjustmentWritePlatformService adjustmentWritePlatformService,final FromJsonHelper fromJsonHelper,
		final OrderWritePlatformService orderWritePlatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
		final RedemptionReadPlatformService redemptionReadPlatformService,final OrderRepository orderRepository,final RedemptionCommandFromApiJsonDeserializer apiJsonDeserializer) {
		
		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.orderRepository=orderRepository;
		this.clientRepository = clientRepository;
		this.fromApiJsonDeserializer= apiJsonDeserializer;
		this.orderWritePlatformService = orderWritePlatformService;
		this.redemptionReadPlatformService=redemptionReadPlatformService;
		this.adjustmentWritePlatformService = adjustmentWritePlatformService;
		this.randomGeneratorDetailsRepository = randomGeneratorDetailsRepository;
		this.contractPeriodReadPlatformService = contractPeriodReadPlatformService;
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createRedemption(JsonCommand command) {
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final String pinNum=command.stringValueOfParameterNamed("pinNumber");
			this.clientObjectRetrieveById(clientId);
			 RandomGeneratorDetails randomGeneratorDetails = retrieveRandomDetailsByPinNo(pinNum);
			 RandomGenerator randomGenerator = randomGeneratorDetails.getRandomGenerator();
			 String pinType = randomGenerator.getPinType();
			 
			 if(pinType.equalsIgnoreCase("VALUE")){
				 
				 BigDecimal pinValue = new BigDecimal(randomGenerator.getPinValue());
				 JsonObject json = new JsonObject();
				 json.addProperty("adjustment_type", "CREDIT");json.addProperty("adjustment_code", 123);
				 json.addProperty("amount_paid",pinValue);json.addProperty("Remarks", "Adjustment Post By Redemption");
				 json.addProperty("locale", "en");json.addProperty("dateFormat","dd MMMM yyyy");
				 json.addProperty("adjustment_date", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
				 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null, clientId, null, null, clientId, null, null, null, null, null, null,null);
		          this.adjustmentWritePlatformService.createAdjustments(commd);
			 }
			 if(pinType.equalsIgnoreCase("PRODUCT")){
				 
				 Long planId = Long.parseLong(randomGenerator.getPinValue());
				 List<Long> orderIds=this.redemptionReadPlatformService.retrieveOrdersData(clientId,planId);
				 JsonObject json = new JsonObject();
				 List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1);
				 
				 if(orderIds.isEmpty()){
					 
					 json.addProperty("billAlign", false);json.addProperty("planCode", planId);
					 json.addProperty("contractPeriod", subscriptionDatas.get(0).getId());json.addProperty("isNewplan", true);
					 json.addProperty("paytermCode", "Monthly");json.addProperty("locale", "en");
					 json.addProperty("dateFormat","dd MMMM yyyy"); json.addProperty("start_date", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
					 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null,clientId, null, null, null, null, null, null, null, null, null,null);
					    this.orderWritePlatformService.createOrder(clientId, commd);
				 }else {
					 
					 Long orderId = orderIds.get(0);
					 
					 Order order=this.orderRepository.findOne(orderId);
					 
						if(order.getStatus() == 3){
							
						   this.orderWritePlatformService.reconnectOrder(orderId);
						}
						
						else if(order.getStatus() == 1){
							
							 json.addProperty("renewalPeriod", subscriptionDatas.get(0).getId());
							 json.addProperty("description", "Order Renewal By Redemption");
							 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null, clientId, null, null, clientId, null, null, null, null, null, null,null);
						   this.orderWritePlatformService.renewalClientOrder(commd, orderId);
						}
				 }
			 }
			 
			 
			 randomGeneratorDetails.setClientId(clientId);
			 this.randomGeneratorDetailsRepository.save(randomGeneratorDetails);
			 
			 return new CommandProcessingResult(clientId);
	    }catch(DataIntegrityViolationException dve){
	    	handleCodeDataIntegrityIssues(command, dve);
	    	return new CommandProcessingResult(Long.valueOf(-1));
	    }
		
	}
	
	private RandomGeneratorDetails retrieveRandomDetailsByPinNo(String pinNumber) {
		
			RandomGeneratorDetails randomDetails = this.randomGeneratorDetailsRepository.findOneByPinNumber(pinNumber);
			if(randomDetails == null){throw new PinNumberNotFoundException(pinNumber);}
		return randomDetails;
	}

	private Client clientObjectRetrieveById(Long clientId) {
		
		Client client = this.clientRepository.findOne(clientId);
		if (client== null) { throw new ClientNotFoundException(clientId); }
		return client;
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	        /*if (realCause.getMessage().contains("ClientId does not exist")) {
	        	final Long id = command.longValueOfParameterNamed(""+command.entityId());
	            throw new PlatformDataIntegrityException("error.msg.redemption.clientId.not.exit", "Given ClientId with this '"
	                    + id + "'not exist", "clientId", id);
	        }*/

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}
	
}

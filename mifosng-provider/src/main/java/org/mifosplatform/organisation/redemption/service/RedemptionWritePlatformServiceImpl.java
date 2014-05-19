package org.mifosplatform.organisation.redemption.service;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mifosplatform.finance.adjustment.service.AdjustmentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGenerator;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetails;
import org.mifosplatform.organisation.redemption.domain.RedemptionRepository;
import org.mifosplatform.organisation.redemption.exception.PinNumberNotFoundException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class RedemptionWritePlatformServiceImpl implements
		RedemptionWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(RedemptionWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final JdbcTemplate jdbcTemplate;
	private final RedemptionRepository redemptionRepository;
	private final ClientRepository clientRepository;
	private final AdjustmentWritePlatformService adjustmentWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	
	@Autowired
	public RedemptionWritePlatformServiceImpl(final PlatformSecurityContext context,final RedemptionRepository redemptionRepository,
									final TenantAwareRoutingDataSource dataSource,final ClientRepository clientRepository,
									final AdjustmentWritePlatformService adjustmentWritePlatformService,
									final FromJsonHelper fromJsonHelper,final OrderWritePlatformService orderWritePlatformService,
									final ContractPeriodReadPlatformService contractPeriodReadPlatformService) {
		this.context = context;
		this.redemptionRepository = redemptionRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.clientRepository = clientRepository;
		this.adjustmentWritePlatformService = adjustmentWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.orderWritePlatformService = orderWritePlatformService;
		this.contractPeriodReadPlatformService = contractPeriodReadPlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createRedemption(JsonCommand command) {
		try {
			context.authenticatedUser();
			Long clientId = command.entityId();
			 clientObjectRetrieveById(clientId);
			 RandomGeneratorDetails randomGeneratorDetails = retrieveRandomDetailsByPinNo(command.getSupportedEntityType());
			 RandomGenerator randomGenerator = randomGeneratorDetails.getRandomGenerator();
			 String pinType = randomGenerator.getPinType();
			 if(pinType.equalsIgnoreCase("VALUE")){
				 
				 BigDecimal pinValue = new BigDecimal(randomGenerator.getPinValue());
				 JsonObject json = new JsonObject();
				 json.addProperty("adjustment_type", "DEBIT");json.addProperty("adjustment_code", 123);
				 json.addProperty("amount_paid",pinValue);json.addProperty("Remarks", "Adjustment Post By Redemption");
				 json.addProperty("locale", "en");json.addProperty("dateFormat","dd MMMM yyyy");
				 json.addProperty("adjustment_date", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
				 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null, clientId, null, null, clientId, null, null, null, null, null, null);
		          this.adjustmentWritePlatformService.createAdjustments(commd);
			 }
			 if(pinType.equalsIgnoreCase("PRODUCT")){
				 Long planId = Long.parseLong(randomGenerator.getPinValue());
				 List<OrderData> ordersDatas = retrieveOrdersData(clientId,planId);
				 JsonObject json = new JsonObject();
				 List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1);
				 
				 if(ordersDatas.isEmpty()){
					 json.addProperty("billAlign", false);json.addProperty("planCode", planId);
					 json.addProperty("contractPeriod", subscriptionDatas.get(0).getId());json.addProperty("isNewplan", true);
					 json.addProperty("paytermCode", "Monthly");json.addProperty("locale", "en");
					 json.addProperty("dateFormat","dd MMMM yyyy"); json.addProperty("start_date", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
					 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null,clientId, null, null, null, null, null, null, null, null, null);
					    this.orderWritePlatformService.createOrder(clientId, commd);
				 }else {
					 Long planStatus = ordersDatas.get(0).getPlanStatus();
					 
						if(planStatus == 3){
							
						   this.orderWritePlatformService.reconnectOrder(planId);
						}
						else if(planStatus == 1){
							
							 json.addProperty("renewalPeriod", subscriptionDatas.get(0).getId());
							 json.addProperty("description", "Order Renewal By Redemption");
							 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null, clientId, null, null, clientId, null, null, null, null, null, null);
						   this.orderWritePlatformService.renewalClientOrder(commd, planId);
						}
				 }
			 }
			 return new CommandProcessingResult(clientId);
	    }catch(DataIntegrityViolationException dve){
	    	handleCodeDataIntegrityIssues(command, dve);
	    	return new CommandProcessingResult(Long.valueOf(-1));
	    }
		
	}
	
	@Transactional
	private List<OrderData> retrieveOrdersData(Long clientId,Long planId){
		OrderMapper mapper = new OrderMapper();
		String sql = "select plan_id as planId,order_status as status from b_orders where client_id = ? and plan_id = ? ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId, planId });
	}
	private static final class OrderMapper implements RowMapper<OrderData>{
		
		@Override
		public OrderData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Long planId=rs.getLong("planId");
			Long planStatus=rs.getLong("status");
			return new  OrderData(planId,planStatus);
		}
		
	}
	
	private RandomGeneratorDetails retrieveRandomDetailsByPinNo(String pinNumber) {
		
			RandomGeneratorDetails randomDetails = this.redemptionRepository.findOneByPinNumber(pinNumber);
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

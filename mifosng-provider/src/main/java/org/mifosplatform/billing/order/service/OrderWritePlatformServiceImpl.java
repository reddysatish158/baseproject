package org.mifosplatform.billing.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.billing.association.data.AssociationData;
import org.mifosplatform.billing.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.billing.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.billing.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.billing.billingorder.exceptions.NoPromotionFoundException;
import org.mifosplatform.billing.billingorder.service.ReverseInvoice;
import org.mifosplatform.billing.contract.domain.Contract;
import org.mifosplatform.billing.contract.domain.SubscriptionRepository;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.discountmaster.exceptions.DiscountMasterNoRecordsFoundException;
import org.mifosplatform.billing.eventorder.domain.PrepareRequest;
import org.mifosplatform.billing.eventorder.domain.PrepareRequsetRepository;
import org.mifosplatform.billing.eventorder.service.PrepareRequestWriteplatformService;
import org.mifosplatform.billing.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.data.UserActionStatusEnumaration;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderDiscount;
import org.mifosplatform.billing.order.domain.OrderDiscountRepository;
import org.mifosplatform.billing.order.domain.OrderHistory;
import org.mifosplatform.billing.order.domain.OrderHistoryRepository;
import org.mifosplatform.billing.order.domain.OrderLine;
import org.mifosplatform.billing.order.domain.OrderPrice;
import org.mifosplatform.billing.order.domain.OrderPriceRepository;
import org.mifosplatform.billing.order.domain.OrderReadPlatformImpl;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.domain.Promotion;
import org.mifosplatform.billing.order.domain.PromotionRepository;
import org.mifosplatform.billing.order.exceptions.NoOrdersFoundException;
import org.mifosplatform.billing.order.exceptions.NoRegionalPriceFound;
import org.mifosplatform.billing.order.serialization.OrderCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.payments.api.PaymentsApiResource;
import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.plan.domain.Plan;
import org.mifosplatform.billing.plan.domain.PlanRepository;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.preparerequest.exception.PrepareRequestActivationException;
import org.mifosplatform.billing.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.billing.pricing.data.PriceData;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetails;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetailsRepository;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.domain.AccountNumberGenerator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGeneratorFactory;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class OrderWritePlatformServiceImpl implements OrderWritePlatformService {
	
	private final PlatformSecurityContext context;
	private final OrderRepository orderRepository;
	private final PlanRepository planRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final OrderPriceRepository OrderPriceRepository;
	private final JdbcTemplate jdbcTemplate;
	private final OrderCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final PrepareRequestWriteplatformService prepareRequestWriteplatformService;
    private final DiscountMasterRepository discountMasterRepository;
    private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderReadPlatformService orderReadPlatformService;
    private final ReverseInvoice reverseInvoice;
    private final GlobalConfigurationRepository configurationRepository;
    private final AllocationReadPlatformService allocationReadPlatformService; 
    private final HardwareAssociationWriteplatformService associationWriteplatformService;
    private final ProvisionServiceDetailsRepository provisionServiceDetailsRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final HardwareAssociationReadplatformService hardwareAssociationReadplatformService;
    private final PaymentsApiResource paymentsApiResource;
    private final PrepareRequestReadplatformService prepareRequestReadplatformService;
    private final PrepareRequsetRepository prepareRequsetRepository;
    private final PromotionRepository promotionRepository;
    private final OrderDiscountRepository orderDiscountRepository;
    private final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory;
    
    public final static String CONFIG_PROPERTY="Implicit Association";

    @Autowired
	public OrderWritePlatformServiceImpl(final PlatformSecurityContext context,final OrderRepository orderRepository,
			final PlanRepository planRepository,final OrderPriceRepository OrderPriceRepository,final TenantAwareRoutingDataSource dataSource,
			final SubscriptionRepository subscriptionRepository,final OrderCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ReverseInvoice reverseInvoice,
			final PrepareRequestWriteplatformService prepareRequestWriteplatformService,final DiscountMasterRepository discountMasterRepository,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OrderHistoryRepository orderHistoryRepository,
			final  GlobalConfigurationRepository configurationRepository,final AllocationReadPlatformService allocationReadPlatformService,
			final HardwareAssociationWriteplatformService associationWriteplatformService,final PrepareRequestReadplatformService prepareRequestReadplatformService,
			final ProvisionServiceDetailsRepository provisionServiceDetailsRepository,final OrderReadPlatformService orderReadPlatformService,
		    final ProcessRequestRepository processRequestRepository,final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,
		    final PaymentsApiResource paymentsApiResource,final PrepareRequsetRepository prepareRequsetRepository,final PromotionRepository promotionRepository,
		    final OrderDiscountRepository orderDiscountRepository,final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory) {
		
		this.context = context;
		this.orderRepository = orderRepository;
		this.OrderPriceRepository = OrderPriceRepository;
		this.planRepository = planRepository;
		this.prepareRequestWriteplatformService=prepareRequestWriteplatformService;
		this.subscriptionRepository = subscriptionRepository;
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.discountMasterRepository=discountMasterRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
		this.orderHistoryRepository=orderHistoryRepository;
		this.reverseInvoice=reverseInvoice;
		this.configurationRepository=configurationRepository;
		this.allocationReadPlatformService=allocationReadPlatformService;
		this.associationWriteplatformService=associationWriteplatformService;
		this.provisionServiceDetailsRepository=provisionServiceDetailsRepository;
		this.processRequestRepository=processRequestRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.hardwareAssociationReadplatformService=hardwareAssociationReadplatformService;
		this.paymentsApiResource=paymentsApiResource;
		this.prepareRequestReadplatformService=prepareRequestReadplatformService;
		this.prepareRequsetRepository=prepareRequsetRepository;
		this.promotionRepository=promotionRepository;
		this.orderDiscountRepository=orderDiscountRepository;
		this.accountIdentifierGeneratorFactory=accountIdentifierGeneratorFactory;
		

	}
	
    
	@Override
	public CommandProcessingResult createOrder(Long clientId,JsonCommand command) {
	
		try{
			//context.authenticatedUser().
			 LocalDate endDate = null;
			 Long orderStatus=null;
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			List<OrderLine> serviceDetails = new ArrayList<OrderLine>();
			List<OrderPrice> orderprice = new ArrayList<OrderPrice>();
			List<PriceData> datas = new ArrayList<PriceData>();
			
			     OrderReadPlatformImpl obj = new OrderReadPlatformImpl(context,jdbcTemplate);
                 Order order=Order.fromJson(clientId,command);
			     Plan plan = this.planRepository.findOne(order.getPlanId());
			   
			List<ServiceData> details = obj.retrieveAllServices(order.getPlanId());
			datas=obj.retrieveAllPrices(order.getPlanId(),order.getBillingFrequency(),clientId);
			 if(datas.isEmpty()){
				 datas=obj.retrieveDefaultPrices(order.getPlanId(),order.getBillingFrequency(),clientId);
			  }
			 if(datas.isEmpty()){
				  throw new NoRegionalPriceFound();
			  }
			
			Contract subscriptionData = this.subscriptionRepository.findOne(order.getContarctPeriod());
			LocalDate startDate=new LocalDate(order.getStartDate());
			if(plan.getProvisionSystem().equalsIgnoreCase("None")){
			orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();
			
			}else{
			orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId();
			}
		
			
			//Calculate EndDate
			endDate = calculateEndDate(startDate,subscriptionData.getSubscriptionType(),subscriptionData.getUnits());
		
			order=new Order(order.getClientId(),order.getPlanId(),orderStatus,null,order.getBillingFrequency(),startDate, endDate,
					order.getContarctPeriod(), serviceDetails, orderprice,order.getbillAlign(),UserActionStatusTypeEnum.ACTIVATION.toString());
			BigDecimal priceforHistory=BigDecimal.ZERO;
			for (PriceData data : datas) {

				LocalDate billstartDate = startDate;
				LocalDate billEndDate = null;
   				//end date is null for rc
				  if (data.getChagreType().equalsIgnoreCase("RC")	&& endDate != null) {
					billEndDate = endDate;
				   
				  } else if(data.getChagreType().equalsIgnoreCase("NRC")) {
					billEndDate = billstartDate;
				   }
				  
				  final DiscountMaster discountMaster=this.discountMasterRepository.findOne(data.getDiscountId());
				  if(discountMaster == null){
					  throw new DiscountMasterNoRecordsFoundException();
				  }

				 //If serviceId Not Exist
				 OrderPrice price = new OrderPrice(data.getServiceId(),data.getChargeCode(), data.getCharging_variant(),data.getPrice(), null, data.getChagreType(),
						 data.getChargeDuration(), data.getDurationType(),billstartDate.toDate(), billEndDate,data.isTaxInclusive());
				 order.addOrderDeatils(price);
				 priceforHistory=priceforHistory.add(data.getPrice());
				
				//discount Order
				OrderDiscount orderDiscount=new OrderDiscount(order,price,discountMaster.getId(),discountMaster.getStartDate(),null,discountMaster.getDiscountType(),
						discountMaster.getDiscountRate());
				price.addOrderDiscount(orderDiscount);
			}

			for (ServiceData data : details) {
				OrderLine orderdetails = new OrderLine(data.getPlanId(),data.getServiceType(), plan.getStatus(), 'n');
				order.addServiceDeatils(orderdetails);
			}
	
			this.orderRepository.save(order);
			Long userId=null;
			SecurityContext context = SecurityContextHolder.getContext();
	        if (context.getAuthentication() != null) {
	        	AppUser appUser=this.context.authenticatedUser();
				userId=appUser.getId();
					
	        }else{
	        	userId=new Long(1);
	        }
	        
			boolean isNewPlan=command.booleanPrimitiveValueOfParameterNamed("isNewplan");
			String requstStatus =UserActionStatusTypeEnum.ACTIVATION.toString();
			
			if(isNewPlan){
			     
			
				final AccountNumberGenerator orderNoGenerator = this.accountIdentifierGeneratorFactory.determineClientAccountNoGenerator(order.getId());
				order.updateOrderNum(orderNoGenerator.generate());
				this.orderRepository.save(order);
				
				//Prepare a Requset For Order
			     CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
			
			   //For Transaction History
			     transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "New Order", order.getStartDate(),"Price:"+priceforHistory,
			     "PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"OrderID:"+order.getId(),
			     "BillingAlign:"+order.getbillAlign());
			     
			   //For Order History
				OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),requstStatus,userId);
				this.orderHistoryRepository.save(orderHistory);
			}
			
            //For Plan And HardWare Association
			GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(CONFIG_PROPERTY);
			
			if(configurationProperty.isEnabled()){
			    if(plan.isHardwareReq() == 'Y'){
			    		   List<AllocationDetailsData> allocationDetailsDatas=this.allocationReadPlatformService.retrieveHardWareDetailsByItemCode(clientId,plan.getPlanCode());
			    		   if(!allocationDetailsDatas.isEmpty())
			    		   {
			    				this.associationWriteplatformService.createNewHardwareAssociation(clientId,plan.getId(),allocationDetailsDatas.get(0).getSerialNo(),order.getId());
			    				transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "Implicit Association", new Date(),"Serial No:"
			    				+allocationDetailsDatas.get(0).getSerialNo(),"Item:"+allocationDetailsDatas.get(0).getItemDescription(),"Plan Code:"+plan.getPlanCode());
			    		   }
			    }
		}
			return new CommandProcessingResult(order.getId());	
	}catch (DataIntegrityViolationException dve) {
		handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}
	
	
	//Calculate EndDate
	public LocalDate calculateEndDate(LocalDate startDate,String durationType,Long duration) {
		
		LocalDate contractEndDate = null;
		if (durationType.equalsIgnoreCase("DAY(s)")) {
			
			 contractEndDate = startDate.plusDays(duration.intValue() - 1);
		} else if (durationType.equalsIgnoreCase("MONTH(s)")) {
			
			 contractEndDate = startDate.plusMonths(duration.intValue()).minusDays(1);
		} else if (durationType.equalsIgnoreCase("YEAR(s)")) {
			
			 contractEndDate = startDate.plusYears(duration.intValue()).minusDays(1);
		} else if (durationType.equalsIgnoreCase("week(s)")) {
			
			 contractEndDate = startDate.plusWeeks(duration.intValue()).minusDays(1);
		}
 
		return contractEndDate;
	}
	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
	}
	
    @Transactional
	@Override
	public CommandProcessingResult updateOrderPrice(Long orderId,JsonCommand command) {
		try
		{
		 context.authenticatedUser();
	     final Order order = retrieveOrderBy(orderId);
	     Long orderPriceId=command.longValueOfParameterNamed("priceId");
	     OrderPrice orderPrice=this.OrderPriceRepository.findOne(orderPriceId);
	            orderPrice.setPrice(command);
	            this.OrderPriceRepository.save(orderPrice);
			AppUser appUser=this.context.authenticatedUser();
			Long userId=appUser.getId();
			 
			
			//For Order History
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),null,"Update Price",userId);
			this.orderHistoryRepository.save(orderHistory);
		 
         return new CommandProcessingResultBuilder() //
         .withCommandId(command.commandId()) //
         .withEntityId(order.getId()) //
         .with(null) //
         .build();
	} catch (DataIntegrityViolationException dve) {
		handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}

	private Order retrieveOrderBy(Long orderId) {
		 final Order order= this.orderRepository.findOne(orderId);
	        if (order == null) { throw new CodeNotFoundException(orderId.toString()); }
	        return order;
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteOrder(Long orderId, JsonCommand command) {
		
		Order order = this.orderRepository.findOne(orderId);
		List<OrderLine> orderline = order.getServices();
		List<OrderPrice> orderPrices=order.getPrice();
		
		Plan plan=this.planRepository.findOne(order.getPlanId());
		if(plan.isPrepaid() == 'N' && !plan.getProvisionSystem().equalsIgnoreCase("None")){
			
        List<Long> prepareIds=this.prepareRequestReadplatformService.getPrepareRequestDetails(orderId);
        if(prepareIds.isEmpty()){
           throw new PrepareRequestActivationException();	
        }
        for(Long id:prepareIds){
        	
        	PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(id);
        	prepareRequest.setCancelStatus("CANCEL");
        	this.prepareRequsetRepository.save(prepareRequest);
        }
		}
		
		for(OrderPrice price:orderPrices){
			price.delete();
		}
		for (OrderLine orderData : orderline) {
			orderData.delete();
		}
		order.delete();
		this.orderRepository.save(order);
		
		//For OrderHistory
		//String requstStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.).getValue();
		
		AppUser appUser=this.context.authenticatedUser();
		Long userId=appUser.getId();
		
		//For Order History
		OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),null,"Cancelled",userId);
		this.orderHistoryRepository.save(orderHistory);
		transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "OrderDelete", order.getEndDate(),"Price:"+order.getAllPriceAsString(),"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"BillingAlign:"+order.getbillAlign());

		return new CommandProcessingResult(order.getId());
	}
	
    @Transactional
	@Override
	public CommandProcessingResult disconnectOrder(JsonCommand command,Long orderId ) {
		try {
			
			this.fromApiJsonDeserializer.validateForDisconnectOrder(command.json());
			Order order = this.orderRepository.findOne(orderId);
			LocalDate disconnectionDate=command.localDateValueOfParameterNamed("disconnectionDate");
			LocalDate currentDate = new LocalDate();
			currentDate.toDate();
			List<OrderPrice> orderPrices=order.getPrice();
			for(OrderPrice price:orderPrices){
				price.updateDates(disconnectionDate);
			}
			Plan plan=this.planRepository.findOne(order.getPlanId());
			Long orderStatus=null;
	         if(plan.getProvisionSystem().equalsIgnoreCase("None")){
				
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId();
			}else{
			
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId();
			}if(plan.getBillRule() !=400){ 
	          
				this.reverseInvoice.reverseInvoiceServices(orderId, order.getClientId(),disconnectionDate);
	        }
			order.update(command,orderStatus);
			order.setuserAction(UserActionStatusTypeEnum.DISCONNECTION.toString());
			this.orderRepository.save(order);
			
			//for Prepare Request
			String requstStatus =UserActionStatusTypeEnum.DISCONNECTION.toString();
			CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
			
			//For Order History
			//String requstStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.).getValue();
			Long userId=null;
			SecurityContext context = SecurityContextHolder.getContext();
			
	        if (context.getAuthentication() != null) {
	        	AppUser appUser=this.context.authenticatedUser();
				userId=appUser.getId();
					
	        }else{
	        	userId=new Long(1);
	        }
			
			//For Order History
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),requstStatus,userId);
			this.orderHistoryRepository.save(orderHistory);
 
			//for TransactionHistory
			transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"ORDER_"+UserActionStatusTypeEnum.DISCONNECTION.toString(), new Date(),
					"Price:"+order.getAllPriceAsString(),"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"BillingAlign:"+order.getbillAlign());
			return new CommandProcessingResult(Long.valueOf(order.getId()));
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null,dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult renewalClientOrder(JsonCommand command,Long orderId) {
		
		try{
			
			this.fromApiJsonDeserializer.validateForRenewalOrder(command.json());
			Order orderDetails=this.orderRepository.findOne(orderId);
			
			if(orderDetails == null){
				throw new NoOrdersFoundException(orderId);
			}
			List<OrderPrice>  orderPrices=orderDetails.getPrice();
		    final Long contractPeriod = command.longValueOfParameterNamed("renewalPeriod");
		    Contract contractDetails=this.subscriptionRepository.findOne(contractPeriod);
		    
		    //Get The Plan Details
		   Plan plan=this.planRepository.findOne(orderDetails.getPlanId());
		    LocalDate newStartdate=new LocalDate(orderDetails.getEndDate());
	
		    newStartdate=newStartdate.plusDays(1);
		   /* if(plan.isPrepaid() == 'Y'){
		    	  
		    	  if(topUpDate.isAfter(newStartdate)){
		    		  newStartdate=topUpDate;
		    	  }else{
		    		  
		    		  int days=Days.daysBetween(topUpDate, newStartdate).getDays();
		    		   newStartdate=newStartdate.plusDays(days);
		    	  }
		    }*/
		    
		 
		    LocalDate renewalEndDate=calculateEndDate(newStartdate,contractDetails.getSubscriptionType(),contractDetails.getUnits());
		      orderDetails.setEndDate(renewalEndDate);
		     
                  for(OrderPrice orderprice:orderPrices){
                	  orderprice.setBillEndDate(renewalEndDate);
                	  this.OrderPriceRepository.save(orderprice);
                  }
                  
                  String requstStatus =UserActionStatusTypeEnum.ACTIVATION.toString();

                  if(orderDetails.getEndDate().after(new Date())){
           		   requstStatus=UserActionStatusEnumaration.OrderStatusType(UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY).getValue();
           	   }else{
           		requstStatus=UserActionStatusEnumaration.OrderStatusType(UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY).getValue();
           	   }
                  
                  if(orderDetails.getStatus().equals(StatusTypeEnum.DISCONNECTED.getValue().longValue()) && (!plan.getProvisionSystem().equalsIgnoreCase("None"))){
                	  
          			this.prepareRequestWriteplatformService.prepareNewRequest(orderDetails,plan,UserActionStatusTypeEnum.ACTIVATION.toString());
          			orderDetails.setStatus(StatusTypeEnum.PENDING.getValue().longValue());
          			orderDetails.setuserAction(requstStatus);
      			}else{
      				
      				orderDetails.setStatus(StatusTypeEnum.ACTIVE.getValue().longValue());
      				orderDetails.setuserAction(requstStatus);
      				
      			}
      			
               orderDetails.setRenewalDate(newStartdate.toDate());
		      this.orderRepository.save(orderDetails);
		      final boolean ispaymentEnable = command.booleanPrimitiveValueOfParameterNamed("ispaymentEnable");
		      if(ispaymentEnable){
		    	   JSONObject jsonobject = new JSONObject();
		    	
	                 jsonobject.put("paymentDate",command.localDateValueOfParameterNamed("paymentDate").toString());
	                 jsonobject.put("amountPaid", command.bigDecimalValueOfParameterNamed("amountPaid"));
	                 jsonobject.put("remarks", command.stringValueOfParameterNamed("remarks"));
	                 jsonobject.put("locale", "en");
	                 jsonobject.put("dateFormat","yyyy-MM-dd");
	                 jsonobject.put("paymentCode",command.longValueOfParameterNamed("paymentCode"));
	                 jsonobject.put("recieptNo",command.longValueOfParameterNamed("recieptNo"));
	                 paymentsApiResource.createPayment(orderDetails.getClientId(), jsonobject.toString());

		      }
 			      // For Order History
		      Long userId=null;
		      SecurityContext context = SecurityContextHolder.getContext();
		      if(context.getAuthentication() != null)
		      {
     		      AppUser appUser=this.context.authenticatedUser();
     		      
	   			   userId=appUser.getId();
		      }
				//For Order History
				OrderHistory orderHistory=new OrderHistory(orderDetails.getId(),new LocalDate(),newStartdate,null,"Renewal",userId);
				this.orderHistoryRepository.save(orderHistory);
				
		  	   return new CommandProcessingResult(Long.valueOf(orderDetails.getId()));
			
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null,dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException e) {
			
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
    @Transactional
    @Override
	public CommandProcessingResult reconnectOrder(Long orderId) {
	  try{
		 // this.context.authenticatedUser();
		  Order order=this.orderRepository.findOne(orderId);
		  if(order == null){
			  throw new NoOrdersFoundException(orderId);
		  }
		  
		  final LocalDate startDate=new LocalDate();
		  Long contractId=order.getContarctPeriod();
		  Contract contractPeriod=this.subscriptionRepository.findOne(contractId);
		  LocalDate EndDate=calculateEndDate(startDate,contractPeriod.getSubscriptionType(),contractPeriod.getUnits());
		   order.setStartDate(startDate);
		   order.setEndDate(EndDate);
		   order.setNextBillableDay(null);
		   List<OrderPrice> orderPrices=order.getPrice();
		   
		   for(OrderPrice price:orderPrices){
			   
			   price.setBillStartDate(startDate);
			   price.setBillEndDate(EndDate);
			   price.setNextBillableDay(null);
			   price.setInvoiceTillDate(null);
			   this.OrderPriceRepository.saveAndFlush(price);
			   
		   }
		   Plan plan=this.planRepository.findOne(order.getPlanId());
		      if(plan.getProvisionSystem().equalsIgnoreCase("None")){
					
		    	  order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
				 
		      }else{
					 
					 //Check For HardwareAssociation
					  AssociationData associationData=this.hardwareAssociationReadplatformService.retrieveSingleDetails(orderId);
					  if(associationData ==null){
						  throw new HardwareDetailsNotFoundException(orderId.toString());
					  }
				
					  order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
				}
		   
		 
		        order.setuserAction(UserActionStatusTypeEnum.RECONNECTION.toString());
		      this.orderRepository.save(order);
		   
			//for Prepare Request
			String requstStatus = UserActionStatusTypeEnum.RECONNECTION.toString().toString();
			CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
			
			//For Order History
			Long userId=null;
			SecurityContext context = SecurityContextHolder.getContext();
	        if (context.getAuthentication() != null) {
	        	AppUser appUser=this.context.authenticatedUser();
				userId=appUser.getId();
					
	        }else{
	        	userId=new Long(1);
	        }
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),requstStatus,userId);
			this.orderHistoryRepository.save(orderHistory);
		
			//for TransactionHistory
			transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RECONNECTION.toString(), order.getStartDate(),
					"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
			
		   return new CommandProcessingResult(order.getId());
	  }catch(DataIntegrityViolationException dve){
		  handleCodeDataIntegrityIssues(null, dve);
		  return new CommandProcessingResult(Long.valueOf(-1));
	  }
	}

	@Override
	public CommandProcessingResult retrackOsdMessage(JsonCommand command) {
		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForRetrack(command.json());
				
			String requstStatus = null;
			String message = null;
			String commandName = command.stringValueOfParameterNamed("commandName");
			Order order = this.orderRepository.findOne(command.entityId());
			if (order == null) {
				throw new NoOrdersFoundException(command.entityId());
			}
			 if (commandName.equalsIgnoreCase("RETRACK")) {
				String restrict=orderReadPlatformService.checkRetrackInterval(command.entityId());
				if(restrict!=null && restrict.equalsIgnoreCase("yes")){
				Long id = this.orderReadPlatformService.getRetrackId(command.entityId());				
				String transaction_type = this.orderReadPlatformService.getOSDTransactionType(id);

				if (transaction_type.equalsIgnoreCase("ACTIVATION")) {
					requstStatus = UserActionStatusTypeEnum.ACTIVATION.toString();

				} else if (transaction_type.equalsIgnoreCase("RECONNECTION")) {
					requstStatus = UserActionStatusTypeEnum.RECONNECTION.toString();

				} else if (transaction_type.equalsIgnoreCase("DISCONNECTION")) {
					requstStatus = UserActionStatusTypeEnum.DISCONNECTION.toString();

				} else {
					requstStatus = null;
				}
				}else{
					throw new PlatformDataIntegrityException("retrack.already.done", "retrack.already.done", "retrack.already.done");	
				}

			} else if(commandName.equalsIgnoreCase("OSM")) {
				requstStatus = UserActionStatusTypeEnum.MESSAGE.toString();
				message = command.stringValueOfParameterNamed("message");
			} else{
				requstStatus = UserActionStatusTypeEnum.INVALID.toString();
			}
			 
			 
			Plan plan = this.planRepository.findOne(order.getPlanId());
			
			if (plan == null) {
				throw new NoOrdersFoundException(command.entityId());
			}

			if (requstStatus != null && plan!=null) {
				
				  AllocationDetailsData detailsData = this.allocationReadPlatformService
						.getTheHardwareItemDetails(command.entityId());

				  ProcessRequest processRequest = new ProcessRequest(order.getClientId(),
						order.getId(),plan.getProvisionSystem(), 'N', null, requstStatus,new Long(0));
				
				  processRequest.setNotify();
				
				  List<OrderLine> orderLineData = order.getServices();
				  for (OrderLine orderLine : orderLineData) {

					String HardWareId = null;
					if (detailsData != null) {
						HardWareId = detailsData.getSerialNo();
					}

					ProvisionServiceDetails provisionServiceDetails = this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getServiceId());
					
					if (provisionServiceDetails != null) {
						if (message == null) {
							message = provisionServiceDetails.getServiceIdentification();
						}
						ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLine.getId(), orderLine.getServiceId(),message, "Recieved",
								HardWareId,order.getStartDate(), order.getEndDate(), null,null, 'N',requstStatus);
						processRequest.add(processRequestDetails);
					}
				}
				this.processRequestRepository.save(processRequest);
				
				this.orderRepository.save(order);
				AppUser appUser = this.context.authenticatedUser();
				Long userId = appUser.getId();
				OrderHistory orderHistory = new OrderHistory(order.getId(),
						new LocalDate(), new LocalDate(), command.entityId(),
						requstStatus, userId);
				this.orderHistoryRepository.save(orderHistory);
				transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "ORDER_"+ requstStatus,order.getStartDate(),"PlanId:" + order.getPlanId(),
						"contarctPeriod:" + order.getContarctPeriod(), "OrderID:" + order.getId(),"BillingAlign:" + order.getbillAlign());
				return new CommandProcessingResult(order.getId());
			}else{
				throw new PlatformDataIntegrityException("transaction_type miss match error", "transaction_type miss match error", "transaction_type miss match error");		
			}
			

		} catch (EmptyResultDataAccessException dve) {
			throw new PlatformDataIntegrityException("retrack.already.done", "retrack.already.done", "retrack.already.done");
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	
	@Override
	public CommandProcessingResult changePlan(JsonCommand command, Long entityId) {
		
		try{
			
			Long userId=this.context.authenticatedUser().getId();
			Order order=this.orderRepository.findOne(entityId);
			
			order.updateDisconnectionstate();
		
			this.orderRepository.save(order);
			Plan oldPlan=this.planRepository.findOne(order.getPlanId());
			if(oldPlan.getBillRule() !=400){ 
		          
				this.reverseInvoice.reverseInvoiceServices(order.getId(), order.getClientId(),new LocalDate());
	        }
			CommandProcessingResult result=this.createOrder(order.getClientId(), command);
			
			Order newOrder=this.orderRepository.findOne(result.resourceId());
			newOrder.updateOrderNum(order.getOrderNo());
			newOrder.updateActivationDate(order.getActiveDate());
			newOrder.setuserAction(UserActionStatusTypeEnum.CHANGE_PLAN.toString());
			this.orderRepository.save(newOrder);
			Plan plan=this.planRepository.findOne(newOrder.getPlanId());
			
			//Prepare a Requset For Order
		     CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(newOrder,plan,UserActionStatusTypeEnum.CHANGE_PLAN.toString());
		     
		   //For Order History
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),
					                               UserActionStatusTypeEnum.CHANGE_PLAN.toString(),userId);
			this.orderHistoryRepository.save(orderHistory);
			this.transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"CHANGE_PLAN", new Date(),"Old Order :"+entityId,
					                               " New OrderId :"+result.resourceId());
			
			return new CommandProcessingResult(result.resourceId());
			
			
		}catch(DataIntegrityViolationException exception){
			handleCodeDataIntegrityIssues(command, exception);
			return new CommandProcessingResult(new Long(-1));
		}
		
	}
    
	@Transactional
	@Override
	public CommandProcessingResult applyPromo(JsonCommand command) {
		
		try{
			String username=this.context.authenticatedUser().getUsername();
			this.fromApiJsonDeserializer.validateForPromo(command.json());			
			final Long promoId=command.longValueOfParameterNamed("promoId");
			final LocalDate startDate=command.localDateValueOfParameterNamed("startDate");
			
			Promotion promotion=this.promotionRepository.findOne(promoId);
			if(promotion == null){
				throw new NoPromotionFoundException(promoId);
			}
			Order order=this.orderRepository.findOne(command.entityId());
			List<OrderDiscount> orderDiscounts=order.getOrderDiscount();
			LocalDate enddate=this.calculateEndDate(startDate,promotion.getDurationType(),promotion.getDuration());
			for(OrderDiscount orderDiscount:orderDiscounts){
				   
				orderDiscount.updateDates(promotion.getDiscountRate(),promotion.getDiscountType(),enddate);
				this.orderDiscountRepository.save(orderDiscount);
			}
			
			this.transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"APPLY PROMOTIONCODE",new Date(), "User :"+username,
					"Promotion Code :" +promotion.getPromotionCode(),"Promotion Value" + promotion.getDiscountRate());
			return new CommandProcessingResult(command.entityId());
		
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
		
	
	}

	
 }
	


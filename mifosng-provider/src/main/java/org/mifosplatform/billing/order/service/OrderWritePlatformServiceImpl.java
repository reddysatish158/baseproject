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
import org.mifosplatform.billing.billingorder.service.ReconnectionInvoice;
import org.mifosplatform.billing.billingorder.service.ReverseInvoice;
import org.mifosplatform.billing.contract.domain.Contract;
import org.mifosplatform.billing.contract.domain.SubscriptionRepository;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.discountmaster.exceptions.DiscountMasterNoRecordsFoundException;
import org.mifosplatform.billing.eventorder.service.PrepareRequestWriteplatformService;
import org.mifosplatform.billing.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.data.UserActionStatusEnumaration;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderDiscount;
import org.mifosplatform.billing.order.domain.OrderHistory;
import org.mifosplatform.billing.order.domain.OrderHistoryRepository;
import org.mifosplatform.billing.order.domain.OrderLine;
import org.mifosplatform.billing.order.domain.OrderPrice;
import org.mifosplatform.billing.order.domain.OrderPriceRepository;
import org.mifosplatform.billing.order.domain.OrderReadPlatformImpl;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.exceptions.NoOrdersFoundException;
import org.mifosplatform.billing.order.exceptions.NoRegionalPriceFound;
import org.mifosplatform.billing.order.serialization.OrderCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.payments.api.PaymentsApiResource;
import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.plan.domain.Plan;
import org.mifosplatform.billing.plan.domain.PlanRepository;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
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
    private final ReconnectionInvoice reconnectionInvoice;
    
    
    public final static String CONFIG_PROPERTY="Implicit Association";
    
    
    
	@Autowired
	public OrderWritePlatformServiceImpl(final PlatformSecurityContext context,final OrderRepository orderRepository,
			final PlanRepository planRepository,final OrderPriceRepository OrderPriceRepository,final TenantAwareRoutingDataSource dataSource,
			final SubscriptionRepository subscriptionRepository,final OrderCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ReverseInvoice reverseInvoice,
			final PrepareRequestWriteplatformService prepareRequestWriteplatformService,final DiscountMasterRepository discountMasterRepository,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OrderHistoryRepository orderHistoryRepository,
			final  GlobalConfigurationRepository configurationRepository,final AllocationReadPlatformService allocationReadPlatformService,
			final HardwareAssociationWriteplatformService associationWriteplatformService,
			final ProvisionServiceDetailsRepository provisionServiceDetailsRepository,final OrderReadPlatformService orderReadPlatformService,
		    final ProcessRequestRepository processRequestRepository,final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,

		    final PaymentsApiResource paymentsApiResource,final ReconnectionInvoice reconnectionInvoice) {


		
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
		this.reconnectionInvoice=reconnectionInvoice;
		

	}
	
	@Override
	public CommandProcessingResult createOrder(Long clientId,JsonCommand command) {
	
		try{
			context.authenticatedUser();
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
			
			 LocalDate endDate = null;
			Contract subscriptionData = this.subscriptionRepository.findOne(order.getContarctPeriod());
			LocalDate startDate=new LocalDate(order.getStartDate());
			Long orderStatus=null;

			if(plan.getProvisionSystem().equalsIgnoreCase("None")){
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();
			
			}else{
			
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId();
			}
			
			//Calculate EndDate
			endDate = calculateEndDate(startDate,subscriptionData);
			order=new Order(order.getClientId(),order.getPlanId(),orderStatus,null,order.getBillingFrequency(),startDate, endDate,
					order.getContarctPeriod(), serviceDetails, orderprice,order.getbillAlign(),UserActionStatusTypeEnum.ACTIVATION.toString());
			BigDecimal priceforHistory=BigDecimal.ZERO;
			for (PriceData data : datas) {
				
				LocalDate billstartDate = startDate;
				LocalDate billEndDate = null;
				
				
				// end date is null for rc
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
					  
				 OrderPrice price = new OrderPrice(data.getServiceId(),data.getChargeCode(), data.getCharging_variant(),
						data.getPrice(), null, data.getChagreType(),data.getChargeDuration(), data.getDurationType(),
						billstartDate.toDate(), billEndDate,data.isTaxInclusive());
				order.addOrderDeatils(price);
				priceforHistory=priceforHistory.add(data.getPrice());
				
				//discount Order
				OrderDiscount orderDiscount=new OrderDiscount(order,price,discountMaster.getId(),startDate.toDate(),endDate,
						discountMaster.getDiscountType(),discountMaster.getDiscountRate());
				price.addOrderDiscount(orderDiscount);
				
				
			}

			for (ServiceData data : details) {
				OrderLine orderdetails = new OrderLine(data.getPlanId(),data.getServiceType(), plan.getStatus(), 'n');
				order.addServiceDeatils(orderdetails);
			}
		     
			this.orderRepository.save(order);

			//Prepare a Requset For Order
			String requstStatus =UserActionStatusTypeEnum.ACTIVATION.toString();
			
			CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
			
			AppUser appUser=this.context.authenticatedUser();
			Long userId=appUser.getId();
			
			//For Order History
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),requstStatus,userId);
			this.orderHistoryRepository.save(orderHistory);
			
          //For Plan And HardWare Association
			GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(CONFIG_PROPERTY);
			
			//For Transaction History
			transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "New Order", order.getStartDate(),"Price:"+priceforHistory,
			     "PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"OrderID:"+order.getId(),
			     "BillingAlign:"+order.getbillAlign());
			
			if(configurationProperty.isEnabled()){
				
			    if(plan.isHardwareReq() == 'Y'){
			    	
			    	 //  PlanHardwareMapping hardwareMapping=this.hardwareMappingRepository.findOneByPlanCode(plan.getPlanCode());
			    	   
			    	//   if(hardwareMapping!=null){
			    		   
			    		   List<AllocationDetailsData> allocationDetailsDatas=this.allocationReadPlatformService.retrieveHardWareDetailsByItemCode(clientId,plan.getPlanCode());
			    		   
			    		   if(!allocationDetailsDatas.isEmpty())
			    		   {
			    				this.associationWriteplatformService.createNewHardwareAssociation(clientId,plan.getId(),allocationDetailsDatas.get(0).getSerialNo(),order.getId());
			    				transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "Implicit Association", new Date(),"Serial No:"
			    				+allocationDetailsDatas.get(0).getSerialNo(),"Item:"+allocationDetailsDatas.get(0).getItemDescription(),"Plan Code:"+plan.getPlanCode());
			    				
			    		   }
			    	 //  }
			    }
		}
			return new CommandProcessingResult(order.getId());	
	
	}catch (DataIntegrityViolationException dve) {
		handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}
	
	
	//Calculate EndDate
	public LocalDate calculateEndDate(LocalDate startDate,Contract subscriptionData) {
		
		LocalDate contractEndDate = null;
		if (subscriptionData.getSubscriptionType().equalsIgnoreCase("DAY(s)")) {
			
			 contractEndDate = startDate.plusDays(subscriptionData.getUnits().intValue() - 1);
		} else if (subscriptionData.getSubscriptionType().equalsIgnoreCase("MONTH(s)")) {
			
			 contractEndDate = startDate.plusMonths(subscriptionData.getUnits().intValue()).minusDays(1);
		} else if (subscriptionData.getSubscriptionType().equalsIgnoreCase("YEAR(s)")) {
			
			 contractEndDate = startDate.plusYears(subscriptionData.getUnits().intValue()).minusDays(1);
		} else if (subscriptionData.getSubscriptionType().equalsIgnoreCase("week(s)")) {
			
			 contractEndDate = startDate.plusWeeks(subscriptionData.getUnits().intValue()).minusDays(1);
		}
 
		return contractEndDate;
		
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
	}
	
	@Override
	public CommandProcessingResult updateOrderPrice(Long orderId,JsonCommand command) {
		try
		{
		 context.authenticatedUser();
	     final Order order = retrieveOrderBy(orderId);//retrievePriceBy(orderId);
	     
	    // List<OrderPrice> orderPrices=order.getPrice();
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

	@Override
	public CommandProcessingResult deleteOrder(Long orderId, JsonCommand command) {
		
		Order order = this.orderRepository.findOne(orderId);
		
		List<OrderLine> orderline = order.getServices();
		List<OrderPrice> orderPrices=order.getPrice();
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
		
		
		transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "OrderDelete", order.getEndDate(),"Price:"+order.getPrice(),"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"services"+order.getServices(),"OrderID:"+order.getId(),"BillingAlign:"+order.getbillAlign());
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
			}
	         
	         this.reverseInvoice.reverseInvoiceServices(orderId, order.getClientId(),disconnectionDate);
	      
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
			transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"ORDER_"+UserActionStatusTypeEnum.DISCONNECTION.toString(), order.getStartDate(),
					"Price:"+order.getPrice(),"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"services"+order.getServices(),"OrderID:"+order.getId(),"BillingAlign:"+order.getbillAlign());
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
		//    LocalDate topUpDate=new LocalDate();
		    newStartdate=newStartdate.plusDays(1);
		   /* if(plan.isPrepaid() == 'Y'){
		    	  
		    	  if(topUpDate.isAfter(newStartdate)){
		    		  newStartdate=topUpDate;
		    	  }else{
		    		  
		    		  int days=Days.daysBetween(topUpDate, newStartdate).getDays();
		    		   newStartdate=newStartdate.plusDays(days);
		    	  }
		    }*/
		    
		 
		    LocalDate renewalEndDate=calculateEndDate(newStartdate,contractDetails);
		      orderDetails.setEndDate(renewalEndDate);
                  for(OrderPrice orderprice:orderPrices){
                	  orderprice.setBillEndDate(renewalEndDate);
                	  this.OrderPriceRepository.save(orderprice);
                       //OrderPrice price=this.OrderPriceRepository.findOne(orderprice.)
                	  
                  }
                  
                  String requstStatus =UserActionStatusTypeEnum.ACTIVATION.toString();

                  if(orderDetails.getEndDate().after(new Date())){
           		   requstStatus=UserActionStatusEnumaration.OrderStatusType(UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY).getValue();
           	   }else{
           		requstStatus=UserActionStatusEnumaration.OrderStatusType(UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY).getValue();
           	   }
                  
                  if(orderDetails.getStatus().equals(StatusTypeEnum.DISCONNECTED.getValue()) && (!plan.getProvisionSystem().equalsIgnoreCase("None"))){
                	  
          			this.prepareRequestWriteplatformService.prepareNewRequest(orderDetails,plan,requstStatus);
          			
          			orderDetails.setStatus(StatusTypeEnum.PENDING.getValue().longValue());
          			orderDetails.setuserAction(requstStatus);
      			}else{
      				
      				
      				orderDetails.setStatus(StatusTypeEnum.ACTIVE.getValue().longValue());
      				orderDetails.setuserAction(requstStatus);
      			}
      			
		      
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
		  	

		    	    //this.paymentWritePlatformService.createPayment(command);
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
		  this.context.authenticatedUser();
		  Order order=this.orderRepository.findOne(orderId);
		  if(order == null){
			  throw new NoOrdersFoundException(orderId);
		  }
		  
		  final LocalDate startDate=new LocalDate();
		  Long contractId=order.getContarctPeriod();
		  Contract contractPeriod=this.subscriptionRepository.findOne(contractId);
		  LocalDate EndDate=calculateEndDate(startDate,contractPeriod);
		   order.setStartDate(startDate);
		   order.setEndDate(EndDate);
		   List<OrderPrice> orderPrices=order.getPrice();
		   
		   for(OrderPrice price:orderPrices){
			   
			   price.setBillStartDate(startDate);
			   price.setBillEndDate(EndDate);
			   price.setNextBillableDay(null);
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
		   
		 
		 //  order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
		        order.setuserAction(UserActionStatusTypeEnum.RECONNECTION.toString());
		      this.orderRepository.save(order);
		   
		      this.reconnectionInvoice.reconnectionInvoiceServices(orderId, order.getClientId(), new LocalDate());
			  
			//for Prepare Request
			String requstStatus = UserActionStatusTypeEnum.RECONNECTION.toString().toString();
			CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
			
			//For Order History
			  
		      AppUser appUser=this.context.authenticatedUser();
				Long userId=appUser.getId();
			OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),processingResult.commandId(),requstStatus,userId);
			this.orderHistoryRepository.save(orderHistory);
		
			//for TransactionHistory
			transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RECONNECTION.toString(), order.getStartDate(),
					"Price:"+order.getPrice(),"PlanId:"+order.getPlanId(),"contarctPeriod:"+order.getContarctPeriod(),"services"+order.getServices(),"OrderID:"+order.getId(),"BillingAlign:"+order.getbillAlign());
			
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
						ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(
								orderLine.getId(), orderLine.getServiceId(),
								message, "Recieved", HardWareId,
								order.getStartDate(), order.getEndDate(), null,
								null, 'N');
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
				transactionHistoryWritePlatformService.saveTransactionHistory(
						order.getClientId(), "ORDER_"+ requstStatus,
						order.getStartDate(), "Price:" + order.getPrice(),
						"PlanId:" + order.getPlanId(),
						"contarctPeriod:" + order.getContarctPeriod(), "services"
								+ order.getServices(), "OrderID:" + order.getId(),
						"BillingAlign:" + order.getbillAlign());
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

	
 }
	


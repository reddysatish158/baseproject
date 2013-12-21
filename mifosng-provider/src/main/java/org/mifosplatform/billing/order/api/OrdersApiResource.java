package org.mifosplatform.billing.order.api;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.order.data.OrderData;
import org.mifosplatform.billing.order.data.OrderHistoryData;
import org.mifosplatform.billing.order.data.OrderPriceData;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.billing.plan.data.PlanCodeData;
import org.mifosplatform.billing.plan.service.PlanReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/orders")
@Component
@Scope("singleton")
public class OrdersApiResource {
	private static final String CONFIG_PROPERTY = "renewal";
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","cancelledStatus","status","contractPeriod","nextBillDate","flag",
	           "currentDate","plan_code","units","service_code","allowedtypes","data","servicedata","billing_frequency", "start_date", "contract_period",
	           "billingCycle","startDate","invoiceTillDate","orderHistory","userAction","ispaymentEnable","paymodes"));
	  private final String resourceNameForPermissions = "ORDER";
	  private final PlatformSecurityContext context;
	  private final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer;
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	  private final OrderReadPlatformService orderReadPlatformService;
	  private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	  private final PlanReadPlatformService planReadPlatformService;
	  private final PaymodeReadPlatformService paymodeReadPlatformService;
	  private final GlobalConfigurationRepository configurationRepository;
	  
	  @Autowired
	    public OrdersApiResource(final PlatformSecurityContext context,final GlobalConfigurationRepository configurationRepository,  
	   final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
	   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final OrderReadPlatformService orderReadPlatformService,
	   final PlanReadPlatformService planReadPlatformService,final PaymodeReadPlatformService paymodeReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.planReadPlatformService=planReadPlatformService;
		        this.orderReadPlatformService=orderReadPlatformService;
		        this.paymodeReadPlatformService=paymodeReadPlatformService;
		        this.configurationRepository=configurationRepository;
		    }	
	  
	@POST
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createOrder(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
 	    final CommandWrapper commandRequest = new CommandWrapperBuilder().createOrder(clientId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOrderTemplate(@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	OrderData orderData = handleTemplateRelatedData();
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	}
	
	private OrderData handleTemplateRelatedData() {
		List<PlanCodeData> planDatas = this.orderReadPlatformService.retrieveAllPlatformData();
		List<PaytermData> data=new ArrayList<PaytermData>();
		List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData();
		return new OrderData(planDatas,data,contractPeriod,null);
	}
	
	@GET
	@Path("{planCode}/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBillingFrequency(@PathParam("planCode") final Long planCode,@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	OrderData orderData = handleTemplateRelatedData();
	List<PaytermData> datas  = this.orderReadPlatformService.getChargeCodes(planCode);
	if(datas.size()==0){
		throw new BillingOrderNoRecordsFoundException(planCode);
	}
	orderData.setPaytermData(datas);
	if(datas.get(0).getDuration()!=null){
	orderData.setDuration(datas.get(0).getDuration());
	orderData.setplanType(datas.get(0).getPlanType());
	}
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	}

	@DELETE
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteOrder(@PathParam("orderId") final Long orderId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteOrder(orderId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}

	 @GET
	    @Path("{clientId}/orders")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOrderDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final List<OrderData> clientOrders = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);
                OrderData orderData=new OrderData(clientId,clientOrders);
        
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	    }
	 
	 @GET
	    @Path("{orderId}/orderprice")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOrderPriceDetails(@PathParam("orderId") final Long orderId,
	    	@Context final UriInfo uriInfo) {
	        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        final List<OrderPriceData> priceDatas = this.orderReadPlatformService.retrieveOrderPriceDetails(orderId,null);
	        final List<OrderHistoryData> historyDatas = this.orderReadPlatformService.retrieveOrderHistoryDetails(orderId);
	         OrderData orderDetailsData = this.orderReadPlatformService.retrieveOrderDetails(orderId);
	         orderDetailsData=new OrderData(priceDatas,historyDatas,orderDetailsData);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, orderDetailsData, RESPONSE_DATA_PARAMETERS);
	    }

	 @PUT
		@Path("{orderId}/orderprice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateOrderPrice(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateOrderPrice(orderId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);

		}
	@PUT
		@Path("{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateOrder(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().cancelOrder(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
		}
		
	@GET
    @Path("renewalorder")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveRenewalOrderDetails(@Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    	List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData();
    	GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(CONFIG_PROPERTY);
    	contractPeriod.remove(0);
    	OrderData orderData=new OrderData(null,contractPeriod,configurationProperty.isEnabled());
    	if(configurationProperty.isEnabled()){
    		Collection<McodeData> data = this.paymodeReadPlatformService.retrievemCodeDetails("Payment Mode");
    		orderData.setPaymodeData(data);
    	}
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
    }
	
	
	@POST
		@Path("renewal/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String renewalOrder(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().renewalOrder(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
		}
	
	 
	 @GET
	    @Path("disconnect")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOrderDisconnectDetails(@Context final UriInfo uriInfo) {
	        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        final Collection<McodeData> disconnectDetails = this.paymodeReadPlatformService.retrievemCodeDetails("Disconnect Reason");
	        OrderData orderData=new OrderData(disconnectDetails,null, false);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	    }
	 
	 
	 @PUT
		@Path("reconnect/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String reconnectOrder(@PathParam("orderId") final Long orderId) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().reconnectOrder(orderId).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
		}

	 @GET
	 @Path("{clientId}/activeplans")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveActivePlans(@PathParam("clientId") final Long clientId,@QueryParam("planType") final String planType, @Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<OrderData> datas=this.orderReadPlatformService.getActivePlans(clientId,planType);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, datas, RESPONSE_DATA_PARAMETERS);
		}
	 
	 @POST
		@Path("retrackOsdmessage/{id}/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrackmessage(@PathParam("id") final Long id,@PathParam("orderId") final Long orderId,
				final String apiRequestBodyAsJson) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder()
					.retrackOsdmessage(orderId,id).withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService
					.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);

	 }
	 

}

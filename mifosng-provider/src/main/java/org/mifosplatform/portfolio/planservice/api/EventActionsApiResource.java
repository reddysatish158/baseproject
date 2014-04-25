package org.mifosplatform.portfolio.planservice.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.SubscriptionRepository;
import org.mifosplatform.portfolio.order.data.SchedulingOrderData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/eventactions")
@Component
@Scope("singleton")
public class EventActionsApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("serviceid","clientId","channelName","image","url"));
    private final String resourceNameForPermissions = "EVENTACTIONS";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<SchedulingOrderData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService; 
	private final PlanRepository planRepository;
	private final SubscriptionRepository subscriptionRepository;
	
	    
	     @Autowired
	    public EventActionsApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<SchedulingOrderData> toApiJsonSerializer, 
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
	    		final PlanRepository planRepository,final SubscriptionRepository subscriptionRepository)
	     {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		        this.planRepository =planRepository;
		        this.subscriptionRepository=subscriptionRepository;
		        
		    }

	        @GET
	        @Path("{clientId}")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String getClientPlanService(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
	        	
			   context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
				final Collection<SchedulingOrderData> schedulingOrderDatas = this.actionDetailsReadPlatformService.retrieveClientSchedulingOrders(clientId);
				for(SchedulingOrderData orderData:schedulingOrderDatas){
					Plan plan=this.planRepository.findOne(orderData.getPlanId());
					Contract contract=this.subscriptionRepository.findOne(orderData.getContractId());
					orderData.setPlandesc(plan.getPlanCode());
					orderData.setContract(contract.getSubscriptionPeriod());
					
				}
				final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
				return this.toApiJsonSerializer.serialize(settings, schedulingOrderDatas, RESPONSE_DATA_PARAMETERS);
			}
}

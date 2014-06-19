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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.planservice.data.PlanServiceData;
import org.mifosplatform.portfolio.planservice.service.PlanServiceReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/planservices")
@Component
@Scope("singleton")
public class PlanServicesApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("serviceid","clientId","channelName","image","url","channelDescription",
			"category","subCategory"));
    private final String resourceNameForPermissions = "PLANSERVICE";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<PlanServiceData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PlanServiceReadPlatformService planServiceReadPlatformService; 

   
	    
	     @Autowired
	    public PlanServicesApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<PlanServiceData> toApiJsonSerializer, 
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PlanServiceReadPlatformService planServiceReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.planServiceReadPlatformService=planServiceReadPlatformService;
		      
		       
		    }

	        @GET
	        @Path("{clientId}")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String getClientPlanService(@PathParam("clientId") final Long clientId,
					@QueryParam("serviceType") final String serviceType,@Context final UriInfo uriInfo) {
	        	
			   context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

			  /* GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);

		//	   GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);

			   
			/*   if(configurationProperty.isEnabled()){
			   
				   ClientBalanceData balanceData = this.clientBalanceReadPlatformService.retrieveBalance(clientId);
			   
				   if(balanceData.getBalanceAmount().compareTo(BigDecimal.ZERO) != -1){
				       throw new InsufficientAmountException(clientId);
				   }
			   }*/
				final Collection<PlanServiceData> masterOptionsDatas = this.planServiceReadPlatformService.retrieveClientPlanService(clientId,serviceType);
				final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
				return this.toApiJsonSerializer.serialize(settings, masterOptionsDatas, RESPONSE_DATA_PARAMETERS);
			}
}

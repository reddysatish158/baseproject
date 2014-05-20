package org.mifosplatform.organisation.hardwareplanmapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.organisation.hardwareplanmapping.service.HardwarePlanReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/hardwaremapping")
@Component
@Scope("singleton")
public class HardwarePlanMappingApiResource {

	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "planCode", "itemCode"));
	 private final String resourceNameForPermissions = "PLANMAPPING";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<HardwarePlanData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final HardwarePlanReadPlatformService HardwarePlanReadPlatformService;
	    
	    @Autowired
	    public HardwarePlanMappingApiResource(final PlatformSecurityContext context, 
	   final DefaultToApiJsonSerializer<HardwarePlanData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
	   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final HardwarePlanReadPlatformService HardwarePlanReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.HardwarePlanReadPlatformService=HardwarePlanReadPlatformService;
		    }	
	    
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllPlans(@QueryParam("itemCode") final String itemCode,  @Context final UriInfo uriInfo) {
 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<HardwarePlanData> products = this.HardwarePlanReadPlatformService.retrievePlanData(itemCode);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, products, RESPONSE_DATA_PARAMETERS);
	}
	

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanTemplate(@Context final UriInfo uriInfo) {
		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
     List<ItemData> itemsData = this.HardwarePlanReadPlatformService.retrieveItems();
     List<PlanCodeData> plansData = this.HardwarePlanReadPlatformService.retrievePlans();

     HardwarePlanData planData = new HardwarePlanData(itemsData, plansData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, planData, RESPONSE_DATA_PARAMETERS);
	
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPlan(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest=new CommandWrapperBuilder().createHardwarePlan().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("{planMapId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePlanMapping(@PathParam("planMapId") final Long planMapId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePlanMapping(planMapId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{planId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDetails(@PathParam("planId") final Long planId,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		HardwarePlanData singlePlandata = this.HardwarePlanReadPlatformService.retrieveSinglePlanData(planId);
		List<ItemData> data = this.HardwarePlanReadPlatformService.retrieveItems();
		List<PlanCodeData> planData = this.HardwarePlanReadPlatformService.retrievePlans();
			singlePlandata.addData(data);
			singlePlandata.addPlan(planData);
			
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings, singlePlandata, RESPONSE_DATA_PARAMETERS);
	}
	
}
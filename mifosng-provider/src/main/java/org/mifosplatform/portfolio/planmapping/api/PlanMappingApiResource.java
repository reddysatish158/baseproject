package org.mifosplatform.portfolio.planmapping.api;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.planmapping.data.PlanMappingData;
import org.mifosplatform.portfolio.planmapping.service.PlanMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/planmapping")
@Component
@Scope("singleton")
public class PlanMappingApiResource {

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "planId", "planIdentification", "status"));

	private final String resourceNameForPermissions = "PROVISIONINGPLANMAPPING";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<PlanMappingData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PlanMappingReadPlatformService planMappingReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;

	@Autowired
	public PlanMappingApiResource(
			final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<PlanMappingData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PlanMappingReadPlatformService planMappingReadPlatformService,
			final PlanReadPlatformService planReadPlatformService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.planMappingReadPlatformService = planMappingReadPlatformService;
		this.planReadPlatformService=planReadPlatformService;
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getPlanMapping(@Context final UriInfo uriInfo) {
		
		List<PlanMappingData> planMapping = this.planMappingReadPlatformService.getPlanMapping();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, planMapping, RESPONSE_DATA_PARAMETERS); 
		
	}
	

	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getTemplateRelatedData(@Context final UriInfo uriInfo) {
		List<PlanCodeData> planCodeData = this.planMappingReadPlatformService.getPlanCode();
		List<EnumOptionData> status = this.planReadPlatformService.retrieveNewStatus();
		PlanMappingData planMappingData = new PlanMappingData(planCodeData,status);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, planMappingData,RESPONSE_DATA_PARAMETERS);
	}
	
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String addServiceMapping(@Context final UriInfo uriInfo, String jsonRequestBody) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createProvisioningPlanMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result  = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{planMappingId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getServiceMappingForEdit(@PathParam("planMappingId") final Long planMappingId, @Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		PlanMappingData planMappingData = this.planMappingReadPlatformService.getPlanMapping(planMappingId);
		List<EnumOptionData> status = this.planReadPlatformService.retrieveNewStatus();
		List<PlanCodeData> planCodeData = this.planMappingReadPlatformService.getPlanCode();
		 planMappingData.setStatus(status);
		 planMappingData.setPlanCodeData(planCodeData); 
	   final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
       return this.toApiJsonSerializer.serialize(settings, planMappingData, RESPONSE_DATA_PARAMETERS); 
	}
	
	@PUT
	@Path("{planMappingId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateServiceMapping(@PathParam("planMappingId") final Long planMappingId, final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateProvisioningPlanMapping(planMappingId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		 return this.toApiJsonSerializer.serialize(result);
	}

}

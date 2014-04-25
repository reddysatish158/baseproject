package org.mifosplatform.workflow.eventactionmapping.api;

import java.util.Arrays;
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

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.workflow.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.workflow.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/eventactionmapping")
@Component
@Scope("singleton")
public class EventActionMappingApiResource {
	
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "action","event","process"));
	 private final String resourceNameForPermissions = "EVENTACTIONMAP";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<EventActionMappingData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final EventActionMappingReadPlatformService eventActionMappingReadPlatformService;
	    private final PlanReadPlatformService planReadPlatformService;
	    private final MCodeReadPlatformService mCodeReadPlatformService;
	    
	    @Autowired
	    public EventActionMappingApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<EventActionMappingData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final EventActionMappingReadPlatformService eventActionMappingReadPlatformService,final PlanReadPlatformService planReadPlatformService,final MCodeReadPlatformService codeReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.eventActionMappingReadPlatformService=eventActionMappingReadPlatformService;
		        this.planReadPlatformService=planReadPlatformService;
		        this.mCodeReadPlatformService=codeReadPlatformService;
		    }		
	    
	  
		@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllEventActionMappingDetails(@QueryParam("planType") final String planType,  @Context final UriInfo uriInfo) {
	 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			List<EventActionMappingData> eventActionDatas= this.eventActionMappingReadPlatformService.retrieveAllEventMapping();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings,eventActionDatas, RESPONSE_DATA_PARAMETERS);
		}
	    
		@GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveEventActionMapTemplate(@Context final UriInfo uriInfo) {
			 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			 
	     List<McodeData> templateData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Action");
	     List<McodeData> templateData1 = this.eventActionMappingReadPlatformService.retrieveEventMapData("Events");
	     EventActionMappingData  data= new EventActionMappingData(templateData,templateData1);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		
		}
	
		@POST
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createEventActionMapping(final String apiRequestBodyAsJson) {

		    final CommandWrapper commandRequest=new CommandWrapperBuilder().createEventActionMapping().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}
		
		@GET
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveSingleDiscountDetails(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			EventActionMappingData discountMasterData= this.eventActionMappingReadPlatformService.retrieveEventActionDetail(id);
			List<McodeData> actionData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Action");
		     List<McodeData> eventData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Events");
		     discountMasterData.setEventData(eventData);
		     discountMasterData.setActionData(actionData);
		     final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			 return this.toApiJsonSerializer.serialize(settings, discountMasterData, RESPONSE_DATA_PARAMETERS);
		}
		
		
		@PUT
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateEventAction(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateEventActionMapping(id).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);
		}
		
	
		 @DELETE
			@Path("{id}")
			@Consumes({MediaType.APPLICATION_JSON})
			@Produces({MediaType.APPLICATION_JSON})
			public String deleteEventAction(@PathParam("id") final Long id) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteEventActionMapping(id).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);

			}

}

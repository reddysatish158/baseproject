package org.mifosplatform.workflow.eventvalidation.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.mifosplatform.workflow.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.mifosplatform.workflow.eventvalidation.data.EventValidationData;
import org.mifosplatform.workflow.eventvalidation.service.EventValidationReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/eventvalidation")
@Component
@Scope("singleton")
public class EventValidationApiResource {
	
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","event","process"));
	 private final String resourceNameForPermissions = "EVENTVALIDATION";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<EventValidationData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final EventActionMappingReadPlatformService eventActionMappingReadPlatformService;
	    private final EventValidationReadPlatformService eventValidationReadPlatformService;
	    private final MCodeReadPlatformService mCodeReadPlatformService;
	    
	    @Autowired
	    public EventValidationApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<EventValidationData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final EventActionMappingReadPlatformService eventActionMappingReadPlatformService,final EventValidationReadPlatformService eventValidationReadPlatformService,final MCodeReadPlatformService codeReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.eventActionMappingReadPlatformService=eventActionMappingReadPlatformService;
		        this.eventValidationReadPlatformService=eventValidationReadPlatformService;
		        this.mCodeReadPlatformService=codeReadPlatformService;
		    }		
	    
	  
		@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllEventActionMappingDetails(@Context final UriInfo uriInfo) {
	 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			List<EventValidationData> eventActionDatas= this.eventValidationReadPlatformService.retrieveAllEventValidation();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings,eventActionDatas, RESPONSE_DATA_PARAMETERS);
		}
	    
		@GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveEventActionMapTemplate(@Context final UriInfo uriInfo) {
			 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	     List<McodeData> templateData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Events");
	     EventValidationData  data= new EventValidationData(templateData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		
		}
	
		@POST
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createEventActionMapping(final String apiRequestBodyAsJson) {
			
		    final CommandWrapper commandRequest=new CommandWrapperBuilder().createEventValidation().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}
		
		@DELETE
		@Path("{id}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deleteEventAction(@PathParam("id") final Long id) {
			
			final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteEventValidation(id).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);

		}

}

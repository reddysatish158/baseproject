package org.mifosplatform.billing.eventorder.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.eventmaster.data.EventMasterData;
import org.mifosplatform.billing.eventmaster.service.EventMasterReadPlatformService;
import org.mifosplatform.billing.eventorder.data.EventOrderData;
import org.mifosplatform.billing.eventorder.data.EventOrderDeviceData;
import org.mifosplatform.billing.eventorder.service.EventOrderReadplatformServie;
import org.mifosplatform.billing.eventorder.service.EventOrderReadplatformServieImpl;
import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.billing.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/eventorder")
@Component
@Scope("singleton")
public class EventOrderApiResource {
			private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("eventId","eventBookedDate","eventValidDate","clientId",
					   "eventPriceId","movieLocation","bookedPrice","eventStatus","chargeCode"));
		    private final String resourceNameForPermissions = "EVENTORDER";
		    private final PlatformSecurityContext context;
			private final DefaultToApiJsonSerializer<EventOrderData> toApiJsonSerializer;
			private final ApiRequestParameterHelper apiRequestParameterHelper;
			private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
			private final FromJsonHelper fromJsonHelper;
			private final EventOrderReadplatformServie eventOrderReadplatformServie; 
			private final EventMasterReadPlatformService eventMasterReadPlatformService;
			private final MCodeReadPlatformService codeReadPlatformService;
			
			
			@Autowired
			public EventOrderApiResource(
					final PlatformSecurityContext context,
					final DefaultToApiJsonSerializer<EventOrderData> toApiJsonSerializer,
					final ApiRequestParameterHelper apiRequestParameterHelper,
					final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
					final FromJsonHelper fromJsonHelper,
					final EventOrderReadplatformServie eventOrderReadplatformServie,
					final EventMasterReadPlatformService eventMasterReadPlatformService,
					final MCodeReadPlatformService codeReadPlatformService) {
				this.context = context;
				this.toApiJsonSerializer = toApiJsonSerializer;
				this.apiRequestParameterHelper = apiRequestParameterHelper;
				this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
				this.fromJsonHelper = fromJsonHelper;
				this.eventOrderReadplatformServie = eventOrderReadplatformServie;
				this.eventMasterReadPlatformService = eventMasterReadPlatformService;
				this.codeReadPlatformService = codeReadPlatformService;
			}

		@POST
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createNewEventOrder(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
			
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createEventOrder().withJson(apiRequestBodyAsJson).build();
		    final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		    return this.toApiJsonSerializer.serialize(result);
			
		}
		
		@GET
		@Path("{clientId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String getEventOrder(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo){			
			context.authenticatedUser();
			
			final List<EventOrderDeviceData> devices = eventOrderReadplatformServie.getDevices(clientId);
			final List<EventMasterData> events = eventOrderReadplatformServie.getEvents();
			final List<EnumOptionData> optType = this.eventMasterReadPlatformService.retrieveOptTypeData();
			final Collection<MCodeData> codes = this.codeReadPlatformService.getCodeValue("MediaFormat");
			final EventOrderData data = new EventOrderData(devices,events,optType,codes);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		}

}

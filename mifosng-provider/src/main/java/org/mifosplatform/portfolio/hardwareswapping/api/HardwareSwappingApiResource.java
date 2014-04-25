package org.mifosplatform.portfolio.hardwareswapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/hardwareswapping")
@Component
@Scope("singleton")
public class HardwareSwappingApiResource {

	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "hardware", "serialNumber","itemCode"));
	
	 private final String resourceNameForPermissions = "HARDWARESWAPPING";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<AssociationData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final HardwareAssociationReadplatformService associationReadplatformService;
	    
	    @Autowired
	    public HardwareSwappingApiResource(final PlatformSecurityContext context, 
	   final DefaultToApiJsonSerializer<AssociationData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
	   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final HardwareAssociationReadplatformService associationReadplatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper; 
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.associationReadplatformService=associationReadplatformService;
		    }		
	
	    @GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrievehardwareAllocationData(@QueryParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
	 		 
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			List<AssociationData> associationDatas = this.associationReadplatformService.retrieveClientAssociationDetails(clientId);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings, associationDatas, RESPONSE_DATA_PARAMETERS);
		}
	    
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String hardwareSwapping(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().hardwareSwapping(clientId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	

}
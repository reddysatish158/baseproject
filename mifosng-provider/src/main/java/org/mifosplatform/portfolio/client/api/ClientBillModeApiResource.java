package org.mifosplatform.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
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
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


	@Path("/billmodes")
	@Component
	@Scope("singleton")
	public class ClientBillModeApiResource {
		private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","billMode"));
	    private final String resourceNameForPermissions = "BILLMODE";
		  private final PlatformSecurityContext context;
		    private final DefaultToApiJsonSerializer<ClientCategoryData> toApiJsonSerializer;
		    private final ApiRequestParameterHelper apiRequestParameterHelper;
		    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
		    private final ClientReadPlatformService clientReadPlatformService;
		    
		    
			 @Autowired
		    public ClientBillModeApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<ClientCategoryData> toApiJsonSerializer, 
		    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
		    		final ClientReadPlatformService clientReadPlatformService){
			        this.context = context;
			        this.toApiJsonSerializer = toApiJsonSerializer;
			        this.apiRequestParameterHelper = apiRequestParameterHelper;
			        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
			        this.clientReadPlatformService =  clientReadPlatformService;
				
			    }		 
			 
		        @GET
			    @Path("{clientId}")
			    @Consumes({ MediaType.APPLICATION_JSON })
			    @Produces({ MediaType.APPLICATION_JSON })
			    public String getBillModeData(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
			    	
			    	 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			    	 ClientCategoryData clientBillModes=this.clientReadPlatformService.retrieveClientBillModes(clientId);
			        return this.toApiJsonSerializer.serialize(clientBillModes);
			    }


			    @PUT
			    @Path("{clientId}")
			    @Consumes({ MediaType.APPLICATION_JSON })
			    @Produces({ MediaType.APPLICATION_JSON })
			    public String update(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
			    	
			    	 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateBillMode(clientId).withJson(apiRequestBodyAsJson).build();
			        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			        return this.toApiJsonSerializer.serialize(result);
			    }

}

	
	



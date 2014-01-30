package org.mifosplatform.organisation.address.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressManageData;
import org.mifosplatform.organisation.address.service.AddressManageReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/addressmanage")
@Component
@Scope("singleton")
public class AddressApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("countryName", "cityName", "stateName"));
	
	private final String resourceNameForPermissions = "ADDRESS";
	
	private final PlatformSecurityContext  context;
	private final AddressManageReadPlatformService readManagePlatformService;
	private final DefaultToApiJsonSerializer<AddressManageData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	@Autowired
	public AddressApiResource(final PlatformSecurityContext  context,final AddressManageReadPlatformService readManagePlatformService,
			final DefaultToApiJsonSerializer<AddressManageData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService){
		this.context=context;
		this.readManagePlatformService=readManagePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAddress(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,  @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchAddresses =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<AddressManageData> addresses = this.readManagePlatformService.retrieveAllAddresses(searchAddresses);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		 return this.toApiJsonSerializer.serialize(addresses);
	}
	
	
}

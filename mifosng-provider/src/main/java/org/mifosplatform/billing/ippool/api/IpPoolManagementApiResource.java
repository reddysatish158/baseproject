package org.mifosplatform.billing.ippool.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.ippool.data.IpPoolManagementData;
import org.mifosplatform.billing.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/ippooling")
@Component
@Scope("singleton")

public class IpPoolManagementApiResource {

	private static final Set<String> IPPOOL_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "ipPoolDescription",
			"ipAddress","subnet"));
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	
	private final String resourceNameForPermissions="READ_IPPOOLMANAGEMENT";
	
	
	
	@Autowired
	public IpPoolManagementApiResource(final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
			final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer,
			final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService)
	{
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.context=context;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
		
	}
	
	@POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCode(final String apiRequestBodyAsJson) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createIpPoolManagement().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllDetailsForPayments(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,@QueryParam("tabType") final String type) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		Page<IpPoolManagementData> paymentData = ipPoolManagementReadPlatformService.retrieveIpPoolData(searchItemDetails,type);
		return this.toApiJsonSerializer.serialize(paymentData);

	}
	
}





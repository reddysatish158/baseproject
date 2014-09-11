package org.mifosplatform.organisation.ippool.api;

import java.util.Arrays;
import java.util.Collection;
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
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/ippooling")
@Component
@Scope("singleton")

public class IpPoolManagementApiResource {

	private static final Set<String> IPPOOL_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "ipPoolDescription",
			"ipAddress","subnet","clientId","clientName","status","codeValueDatas"));
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PlatformSecurityContext context;
	
	private final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	private final MCodeReadPlatformService codeReadPlatformService;
	
	private final String resourceNameForPermissions="READ_IPPOOLMANAGEMENT";
	
	
	
	@Autowired
	public IpPoolManagementApiResource(final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PlatformSecurityContext context,final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer,
			final MCodeReadPlatformService codeReadPlatformService,final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService
			)
	{
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.context=context;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
		this.codeReadPlatformService=codeReadPlatformService;
		
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
	public String retrieveAllIpPoolData(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,@QueryParam("status") final String status) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		  String[] data=null;
		if(sqlSearch !=null && sqlSearch.contains("/")){
  		  sqlSearch.trim();
  		IpGeneration ipGeneration=new IpGeneration(sqlSearch,this.ipPoolManagementReadPlatformService);
             data=ipGeneration.getInfo().getsubnetAddresses();
			
			}
		Page<IpPoolManagementData> paymentData = ipPoolManagementReadPlatformService.retrieveIpPoolData(searchItemDetails,status,data);
		return this.toApiJsonSerializer.serialize(paymentData);

	}
	
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTemplateDataforIppool(@Context final UriInfo uriInfo) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Collection<MCodeData> codeValueDatas=this.codeReadPlatformService.getCodeValue("IP Type");
		IpPoolData ipPoolData=new IpPoolData(codeValueDatas);
		return this.toApiJsonSerializer.serialize(ipPoolData);

	}
	
	@GET
	@Path("search")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveIpPoolIDs(@Context final UriInfo uriInfo,@QueryParam("query") final String query) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<String> ippoolDatas = ipPoolManagementReadPlatformService.retrieveIpPoolIDArray(query);
		IpPoolManagementData data=new IpPoolManagementData(ippoolDatas);
		return this.toApiJsonSerializer.serialize(data);

	}
	
	@PUT
	@Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String editIpPool(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpPoolManagement(id).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	
	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientIpPoolDetails(@Context final UriInfo uriInfo,@PathParam("clientId") final Long clientId) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<IpPoolManagementData> ipPoolManagementDatas = ipPoolManagementReadPlatformService.retrieveClientIpPoolDetails(clientId);
		return this.toApiJsonSerializer.serialize(ipPoolManagementDatas);
	}
	
	@PUT
	@Path("ping/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String gatStatusOfIP(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpStatus(id).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateNetMaskAsDescription(final String apiRequestBodyAsJson) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpPoolDescription().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@GET
	@Path("id/{poolId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSingleIpPoolDetails(@Context final UriInfo uriInfo,@PathParam("poolId") final Long poolId) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Collection<MCodeData> codeValueDatas=this.codeReadPlatformService.getCodeValue("IP Type");
		List<IpPoolManagementData> ipPoolManagementDatas = ipPoolManagementReadPlatformService.retrieveSingleIpPoolDetails(poolId);
		IpPoolData singleIpPoolData=new IpPoolData(codeValueDatas,ipPoolManagementDatas);
		return this.toApiJsonSerializer.serialize(singleIpPoolData);
	}
}





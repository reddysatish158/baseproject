package org.mifosplatform.logistics.agent.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
import org.mifosplatform.logistics.agent.data.AgentItemSaleData;
import org.mifosplatform.logistics.agent.service.AgentReadPlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Path("agents")
@Component
@Scope("singleton")
public class AgentAPiResource {
	
	private static final Set<String> RESPONSE_AGENT_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("agentId",
			"agentName","purchaseDate","orderQuantity","itemCode","itemId","invoiceAmount","taxAmount","chargeAmount","itemPrice"));
	private final String resourceNameForPermissions = "AGENT";
	
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<AgentItemSaleData> toApiJsonSerializer;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ItemReadPlatformService itemReadPlatformService;
	private final AgentReadPlatformService  agentReadPlatformService;
	private final PlatformSecurityContext context;
	
	@Autowired
	public AgentAPiResource(final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,final DefaultToApiJsonSerializer<AgentItemSaleData> apiJsonSerializer,
			final PlatformSecurityContext context,final OfficeReadPlatformService officeReadPlatformService,
			final ItemReadPlatformService itemReadPlatformService,final AgentReadPlatformService  agentReadPlatformService)
	{
		
		this.context=context;
		this.toApiJsonSerializer=apiJsonSerializer;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandSourceWritePlatformService;
		this.officeReadPlatformService=officeReadPlatformService;
		this.itemReadPlatformService=itemReadPlatformService;
		this.agentReadPlatformService=agentReadPlatformService;
	
	}
	
@GET
@Path("template")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public String retrieveTemplateData(@Context final UriInfo uriInfo){
	
	this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	AgentItemSaleData  agentData=handleAgenttemplateData();
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	return this.toApiJsonSerializer.serialize(settings, agentData, RESPONSE_AGENT_DATA_PARAMETERS);
	
}

@POST
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public String createOffice(final String apiRequestBodyAsJson) {

    final CommandWrapper commandRequest = new CommandWrapperBuilder().createItemSale().withJson(apiRequestBodyAsJson).build();
    final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    return this.toApiJsonSerializer.serialize(result);
}

private AgentItemSaleData handleAgenttemplateData() {
	
	List<OfficeData> officeDatas=this.officeReadPlatformService.retrieveAgentTypeData();
	List<ItemData> itemDatas=this.itemReadPlatformService.retrieveAllItems();
	
	return new AgentItemSaleData(officeDatas,itemDatas);
}

@GET
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public String retrieveAllSaleData(@Context final UriInfo uriInfo){
	
	this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	List<AgentItemSaleData> agentDatas=this.agentReadPlatformService.retrieveAllData();
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	return this.toApiJsonSerializer.serialize(settings, agentDatas, RESPONSE_AGENT_DATA_PARAMETERS);
	
}

}

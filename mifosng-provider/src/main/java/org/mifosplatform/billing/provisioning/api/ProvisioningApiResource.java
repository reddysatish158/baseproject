package org.mifosplatform.billing.provisioning.api;

import java.util.Arrays;
import java.util.Collection;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.ippool.data.IpPoolData;
import org.mifosplatform.billing.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.billing.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.billing.order.data.OrderLineData;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.billing.provisioning.data.ProvisioningData;
import org.mifosplatform.billing.provisioning.service.ProvisioningReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/provisionings")
@Component
@Scope("singleton")
public class ProvisioningApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","cancelledStatus","status","contractPeriod","nextBillDate","flag",
	           "currentDate","plan_code","units","service_code","allowedtypes","data","servicedata","billing_frequency", "start_date", "contract_period",
	           "billingCycle","startDate","invoiceTillDate","orderHistory","userAction","ispaymentEnable","paymodes"));
	
	  private final String resourceNameForPermissions = "PROVISIONING";
	  
	  private final PlatformSecurityContext context;
	  private final DefaultToApiJsonSerializer<ProvisioningData> toApiJsonSerializer;
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	  private final ProvisioningReadPlatformService provisioningReadPlatformService;
	  private final MCodeReadPlatformService codeReadPlatformService;
	  private final OrderReadPlatformService orderReadPlatformService;
	  private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	  private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	 
	  
	  @Autowired
	    public ProvisioningApiResource(final PlatformSecurityContext context,final GlobalConfigurationRepository configurationRepository,  
	    final ApiRequestParameterHelper apiRequestParameterHelper,final DefaultToApiJsonSerializer<ProvisioningData> toApiJsonSerializer,
	   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final ProvisioningReadPlatformService provisioningReadPlatformService,
	   final MCodeReadPlatformService codeReadPlatformService,final OrderReadPlatformService orderReadPlatformService,
	   final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService) {
		        this.context = context;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.toApiJsonSerializer=toApiJsonSerializer;
		        this.provisioningReadPlatformService=provisioningReadPlatformService;
		        this.codeReadPlatformService=codeReadPlatformService;
		        this.orderReadPlatformService=orderReadPlatformService;
		        this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
		    }
	
	 @GET
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String retrieveProvisiongSystemDetail(@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<ProvisioningData> datas=this.provisioningReadPlatformService.getProvisioningData();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, datas, RESPONSE_DATA_PARAMETERS);
		}
	 
	 @GET
	 @Path("template")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String retrieveTemplate(@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		
		List<McodeData> provisioning=this.provisioningReadPlatformService.retrieveProvisioningCategory();
		List<McodeData> commands=this.provisioningReadPlatformService.retrievecommands();
		ProvisioningData data=new ProvisioningData(provisioning,commands);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		}
	 
	 @GET
	 @Path("{provisioningId}")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleData(@Context final UriInfo uriInfo,@PathParam("provisioningId") final Long id) {
		 
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);	
		ProvisioningData data= this.provisioningReadPlatformService.retrieveIdData(id);
		List<McodeData> provisioning=this.provisioningReadPlatformService.retrieveProvisioningCategory();
		List<McodeData> commands=this.provisioningReadPlatformService.retrievecommands();
		List<ProvisioningCommandParameterData> commandParameters=this.provisioningReadPlatformService.retrieveCommandParams(id);
		data.setCommands(commands);
		data.setProvisioning(provisioning);
		data.setCommandParameters(commandParameters);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		}
	 
	 @POST
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String addProvisiongSystemDetail(final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().provisiongSystem().withJson(apiRequestBodyAsJson).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);
	}
	 
	 @PUT
	 @Path("{provisioningId}")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String updateProvisiongSystemDetail(@PathParam("provisioningId") final Long id,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateprovisiongSystem(id).withJson(apiRequestBodyAsJson).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);
	} 
	 
	 @DELETE
	 @Path("{provisioningId}")
	 @Consumes({ MediaType.APPLICATION_JSON })
	 @Produces({ MediaType.APPLICATION_JSON })
		public String deleteProvisioningSystem(@PathParam("provisioningId") final Long id,final String apiRequestBodyAsJson) {
			
			 final CommandWrapper commandRequest=new CommandWrapperBuilder().deleteProvisiongSystem(id).withJson(apiRequestBodyAsJson).build();
			   final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			   return this.toApiJsonSerializer.serialize(result);
		}
	 @POST
	 @Path("{clientId}")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
		public String NewActiveProvisioningDetails(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().addNewProvisioning(clientId).withJson(apiRequestBodyAsJson).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);
	}
	 
	 @GET
	 @Path("provisiontemplate/{orderId}")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
	 public String retrieveProvisionTemplateData(@PathParam("orderId") final Long orderId,@Context final UriInfo uriInfo) {
		 
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Collection<MCodeData> vlanDatas=this.codeReadPlatformService.getCodeValue("VLANS");
		List<IpPoolData> ipPoolDatas=this.ipPoolManagementReadPlatformService.getUnallocatedIpAddressDetailds();
		List<OrderLineData> services = this.orderReadPlatformService.retrieveOrderServiceDetails(orderId);
		ProvisioningData provisioningData=new ProvisioningData(vlanDatas,ipPoolDatas,services);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, provisioningData, RESPONSE_DATA_PARAMETERS);
		}

	 

}


package org.mifosplatform.billing.entitlements.api;

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

import org.mifosplatform.billing.entitlements.data.ClientEntitlementData;
import org.mifosplatform.billing.entitlements.data.EntitlementsData;
import org.mifosplatform.billing.entitlements.data.StakerData;
import org.mifosplatform.billing.entitlements.service.EntitlementReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Path("/entitlements")
@Component
@Scope("singleton")
public class EntitlementsApiResource {

	private final static Logger logger = LoggerFactory.getLogger(EntitlementsApiResource.class);
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id,prepareReqId,product,requestType,hardwareId,provisioingSystem,serviceId,results,error,status"));
    private final String resourceNameForPermissions = "CREATE_ENTITLEMENT";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<EntitlementsData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<StakerData> toJsonSerializer;
	private final DefaultToApiJsonSerializer<ClientEntitlementData> toSerializer;
   private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private EntitlementReadPlatformService entitlementReadPlatformService;
	private final DaoAuthenticationProvider customAuthenticationProvider;
	
	@Autowired
	 public EntitlementsApiResource(final PlatformSecurityContext context,  final DefaultToApiJsonSerializer<EntitlementsData> toApiJsonSerializer,
	 final ApiRequestParameterHelper apiRequestParameterHelper, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	 final EntitlementReadPlatformService comvenientReadPlatformService,final DaoAuthenticationProvider customAuthenticationProvider,
	 final DefaultToApiJsonSerializer<StakerData> toJsonSerializer,final DefaultToApiJsonSerializer<ClientEntitlementData> toSerializer)
	{
				        this.context = context;
				        this.toApiJsonSerializer = toApiJsonSerializer;
				        this.apiRequestParameterHelper = apiRequestParameterHelper;
				        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
				        this.entitlementReadPlatformService=comvenientReadPlatformService;
				        this.customAuthenticationProvider=customAuthenticationProvider;
				        this.toJsonSerializer=toJsonSerializer;
				        this.toSerializer=toSerializer;
	}	
	
	@GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrievedata(@QueryParam("no") final Long No,	@Context final UriInfo uriInfo)
	{
	       context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        List<EntitlementsData> data=this.entitlementReadPlatformService.getProcessingData(No);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	@POST
	@Path("{msgId}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String insertdata(@PathParam("msgId") final Long id,final String apiRequestBodyAsJson) {

		   final CommandWrapper commandRequest=new CommandWrapperBuilder().createEntitlement(id).withJson(apiRequestBodyAsJson).build();
		   final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		   return this.toApiJsonSerializer.serialize(result);
		}

	@SuppressWarnings("null")
	@GET
	@Path("/getuser")
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrieveDeviceData(@Context final UriInfo uriInfo,@QueryParam("mac") final String Mac)
	{
	        StakerData data=this.entitlementReadPlatformService.getData(Mac);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        EntitlementsData datas=new EntitlementsData();
	        if(data==null){
	        	 data.setStatus("ERROR");
	        	 data.setError("Does not have any orders"); 
	        	 data.setResults(null);
	         return this.toJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	        }else{
	        datas.setStatus("OK");
	        datas.setResults(data);  
	        return this.toApiJsonSerializer.serialize(settings, datas, RESPONSE_DATA_PARAMETERS);
	        }             
	}
	
	@GET
	@Path("/getauth")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrieveAuthorizeData(@QueryParam("login") final String username, @QueryParam("password") final String password,@Context final UriInfo uriInfo)
	{
		    logger.info("Request comming for Authentication");
	        ClientEntitlementData data=null;
	        StakerData stakerdata=new StakerData();
	        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
	        Authentication authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        
	        if (authenticationCheck.isAuthenticated()) {
		        String status="OK";
		        boolean results=true;
		        data=new ClientEntitlementData(status, results);
		        logger.info("Output - status : "+status+" ,result : "+results);
		        logger.info("Authentication Successful");
		        return this.toSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	        }else{
	        	stakerdata.setStatus("ERROR");
	        	stakerdata.setError("Authentication Failed: 401 unauthorized exception"); 
	        	stakerdata.setResults(null);
	        	logger.info("Output - status : "+stakerdata.getStatus()+" , error : "+stakerdata.getError()+" ,result : "+stakerdata.getResults());
	        	logger.info("Authentication Failure");
	        	return this.toJsonSerializer.serialize(settings, stakerdata, RESPONSE_DATA_PARAMETERS);
	        }
	     
	      
	}
	
	
	
}

package org.mifosplatform.cms.mediadevice.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.cms.mediadetails.exception.NoMediaDeviceFoundException;
import org.mifosplatform.cms.mediadevice.data.MediaDeviceData;
import org.mifosplatform.cms.mediadevice.exception.NoPlanDataFoundException;
import org.mifosplatform.cms.mediadevice.service.MediaDeviceReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/mediadevices")
@Component
@Scope("singleton")
public class MediaDeviceApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("deviceId","clientId","clientType","balanceAmount","balanceCheck"));
	private  final Set<String> RESPONSE_DATA_PARAMETERS_FOR_PLAN=new HashSet<String>(Arrays.asList("id","planCode","planDescription"));
    private final String resourceNameForPermissions = "MEDIADEVICE";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<MediaDeviceData> toApiJsonSerializer;
	    private final DefaultToApiJsonSerializer<PlanData> toApiJsonSerializerForPlanData;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final MediaDeviceReadPlatformService mediaDeviceReadPlatformService;
	    private final GlobalConfigurationRepository configurationRepository;
		
		 @Autowired
	    public MediaDeviceApiResource(final PlatformSecurityContext context,final GlobalConfigurationRepository configurationRepository, 
	   final DefaultToApiJsonSerializer<MediaDeviceData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
	   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final MediaDeviceReadPlatformService mediaDeviceReadPlatformService,
	   final DefaultToApiJsonSerializer<PlanData> toApiJsonSerializerForPlanData,final GlobalConfigurationRepository globalConfigurationRepository){
		        
			 this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.mediaDeviceReadPlatformService=mediaDeviceReadPlatformService;
		        this.toApiJsonSerializerForPlanData = toApiJsonSerializerForPlanData;
		        this.configurationRepository=configurationRepository;

		    }	

		@SuppressWarnings("unused")
		@GET
		@Path("{deviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleDeviceDetails(@PathParam("deviceId") final String deviceId, @Context final UriInfo uriInfo) {
			
			MediaDeviceData datas = this.mediaDeviceReadPlatformService.retrieveDeviceDetails(deviceId);
			
			if(datas == null){
				throw new NoMediaDeviceFoundException();
			}
			
			 GlobalConfigurationProperty paypalConfigData=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IS_PAYPAL_CHECK);
			 datas.setPaypalConfigData(paypalConfigData);
			 GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
			 datas.setBalanceCheck(configurationProperty.isEnabled());
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings,datas, RESPONSE_DATA_PARAMETERS);
		}
		
		@GET
		@Path("{clientId}/prepaid")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrievePlanPrepaidDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
			List<PlanData> datas = this.mediaDeviceReadPlatformService.retrievePlanDetails(clientId);
			if(datas == null){
				throw new NoPlanDataFoundException();
			}
			PlanData planData = new PlanData(datas);
			if(!datas.isEmpty()){
				planData.setIsActive(true);
				planData.setPlanCount(datas.size());
			}else{
				planData.setIsActive(false);
				planData.setPlanCount(datas.size());
			}
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializerForPlanData.serialize(settings,planData, RESPONSE_DATA_PARAMETERS_FOR_PLAN);
		
		
		}
		
		@GET
		@Path("{clientId}/postpaid")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrievePostpaidPlanDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
			List<PlanData> datas = this.mediaDeviceReadPlatformService.retrievePlanPostpaidDetails(clientId);
			if(datas == null){
				throw new NoPlanDataFoundException();
			}
			PlanData planData = new PlanData(datas);
			
			if(!datas.isEmpty()){
				planData.setIsActive(true);
				planData.setPlanCount(datas.size());
			}else{
				planData.setIsActive(false);
				planData.setPlanCount(datas.size());
			}
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializerForPlanData.serialize(settings,planData, RESPONSE_DATA_PARAMETERS_FOR_PLAN);
		
		
		}
	
		@PUT
		@Path("{deviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String updateStatus(@PathParam("deviceId") final String deviceId,final String apiRequestBodyAsJson){
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateMediaStatus(deviceId).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);

		}
		
		@GET
		@Path("client/{clientId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleDeviceDetailsBasedOnClientId(@PathParam("clientId") final String clientId, @Context final UriInfo uriInfo) {
			
			MediaDeviceData datas = this.mediaDeviceReadPlatformService.retrieveClientDetails(clientId);
			
			if(datas == null){
				throw new NoMediaDeviceFoundException();
			}
			GlobalConfigurationProperty paypalConfigData=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IS_PAYPAL_CHECK);
			datas.setPaypalConfigData(paypalConfigData);
			GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
			datas.setBalanceCheck(configurationProperty.isEnabled());
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings,datas, RESPONSE_DATA_PARAMETERS);
		}
}

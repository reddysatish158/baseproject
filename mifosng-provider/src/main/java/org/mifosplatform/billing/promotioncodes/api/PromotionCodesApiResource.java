/*package org.mifosplatform.billing.promotioncodes.api;

public class PromotionCodesApiResource {

}
*/
package org.mifosplatform.billing.promotioncodes.api;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.discountmaster.service.DiscountReadPlatformService;
import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.mifosplatform.billing.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.billing.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.billing.media.data.MediaAssetData;
import org.mifosplatform.billing.media.data.MediaassetAttribute;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.plan.data.BillRuleData;
import org.mifosplatform.billing.plan.data.PlanCodeData;
import org.mifosplatform.billing.plan.data.PlanData;
import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.plan.data.SystemData;
import org.mifosplatform.billing.plan.service.PlanReadPlatformService;
import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;
import org.mifosplatform.billing.promotioncodes.service.PromotionCodeReadPlatformService;
import org.mifosplatform.billing.randomgenerator.data.RandomGeneratorData;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/promotioncode")
@Component
@Scope("singleton")
public class PromotionCodesApiResource {
	
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "action","event","process"));
	 private final String resourceNameForPermissions = "EVENTACTIONMAP";
	  private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer;
	    private final DefaultToApiJsonSerializer<DiscountMasterData> toApiJsonSerializer1;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final EventActionMappingReadPlatformService eventActionMappingReadPlatformService;
	    private final PromotionCodeReadPlatformService promotionCodeReadPlatformService;
	    private final PlanReadPlatformService planReadPlatformService;
	    private final MCodeReadPlatformService mCodeReadPlatformService;
	    
	    @Autowired
	    public PromotionCodesApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final EventActionMappingReadPlatformService eventActionMappingReadPlatformService,final PlanReadPlatformService planReadPlatformService,final MCodeReadPlatformService codeReadPlatformService,
	    		final PromotionCodeReadPlatformService promotionCodeReadPlatformService,final DefaultToApiJsonSerializer<DiscountMasterData> toApiJsonSerializer1) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.eventActionMappingReadPlatformService=eventActionMappingReadPlatformService;
		        this.planReadPlatformService=planReadPlatformService;
		        this.mCodeReadPlatformService=codeReadPlatformService;
		        this.promotionCodeReadPlatformService =promotionCodeReadPlatformService;
		        this.toApiJsonSerializer1=toApiJsonSerializer1;
		    }		
	    
	  
		@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllPromotionDetails(@Context final UriInfo uriInfo) {
	 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			List<PromotionCodeData> promotionDatas= this.promotionCodeReadPlatformService.retrieveAllEventMapping();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings,promotionDatas, RESPONSE_DATA_PARAMETERS);
		}
	    
		@GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrievePromotionTemplate(@Context final UriInfo uriInfo) {
			 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			 
			Collection<MCodeData> discountTypeData = mCodeReadPlatformService.getCodeValue("type");
			PromotionCodeData  data= new PromotionCodeData(discountTypeData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		
		}
		
		@POST
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createPromotionCode(final String apiRequestBodyAsJson) {

		    final CommandWrapper commandRequest=new CommandWrapperBuilder().createPromotionCode().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}
		
		/* @GET
			@Path("template")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String retrieveDiscountTemplate(@Context final UriInfo uriInfo) { 
		    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		    	PromotionCodeData discountMasterData=handleTemplateData();
				final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
				return this.toApiJsonSerializer.serialize(settings, discountMasterData, RESPONSE_DATA_PARAMETERS);
			
			}
			private PromotionCodeData handleTemplateData() {
			     Collection<MCodeData> discountTypeData = mCodeReadPlatformService.getCodeValue("type");
			     return new PromotionCodeData(discountTypeData);
			}*/
	
	
		
	/*	@GET
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveSingleDiscountDetails(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			EventActionMappingData discountMasterData= this.eventActionMappingReadPlatformService.retrieveEventActionDetail(id);
			List<McodeData> actionData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Action");
		     List<McodeData> eventData = this.eventActionMappingReadPlatformService.retrieveEventMapData("Events");
		     discountMasterData.setEventData(eventData);
		     discountMasterData.setActionData(actionData);
		     final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			 return this.toApiJsonSerializer.serialize(settings, discountMasterData, RESPONSE_DATA_PARAMETERS);
		}*/
		
		
		/*@PUT
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateEventAction(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateEventActionMapping(id).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);
		}
		
	
		 @DELETE
			@Path("{id}")
			@Consumes({MediaType.APPLICATION_JSON})
			@Produces({MediaType.APPLICATION_JSON})
			public String deleteEventAction(@PathParam("id") final Long id) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteEventActionMapping(id).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);

			}*/

}

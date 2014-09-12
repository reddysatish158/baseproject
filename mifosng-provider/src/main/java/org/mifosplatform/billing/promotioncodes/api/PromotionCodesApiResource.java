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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;
import org.mifosplatform.billing.promotioncodes.service.PromotionCodeReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.contract.data.PeriodData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.workflow.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/promotioncode")
@Component
@Scope("singleton")
public class PromotionCodesApiResource {
	
	
	  private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "action","event","process"));
	  private final String resourceNameForPermissions = "PROMOTIONCODE";
	  private final PlatformSecurityContext context;
	  private final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer;
	  private final DefaultToApiJsonSerializer<DiscountMasterData> toApiJsonSerializer1;
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	  private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	  private final PromotionCodeReadPlatformService promotionCodeReadPlatformService;
	  private final MCodeReadPlatformService mCodeReadPlatformService;
	  private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	    
	    @Autowired
	    public PromotionCodesApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final EventActionMappingReadPlatformService eventActionMappingReadPlatformService,final PlanReadPlatformService planReadPlatformService,
	    		final DefaultToApiJsonSerializer<DiscountMasterData> toApiJsonSerializer1,final MCodeReadPlatformService codeReadPlatformService,
	    		final PromotionCodeReadPlatformService promotionCodeReadPlatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService) {

		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.mCodeReadPlatformService=codeReadPlatformService;
		        this.promotionCodeReadPlatformService =promotionCodeReadPlatformService;
		        this.toApiJsonSerializer1=toApiJsonSerializer1;
		        this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
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
			List<PeriodData> contractTypedata=contractPeriodReadPlatformService.retrieveAllPlatformPeriod();
			PromotionCodeData  data= new PromotionCodeData(discountTypeData,contractTypedata);

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
	
		@GET
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveSinglePromotionCodeDetails(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			PromotionCodeData promotionCodeData=this.promotionCodeReadPlatformService.retriveSingleRecord(id);
			Collection<MCodeData> discountTypeData = mCodeReadPlatformService.getCodeValue("type");
			List<PeriodData> contractTypedata=contractPeriodReadPlatformService.retrieveAllPlatformPeriod();
			promotionCodeData.setDiscounTypeData(discountTypeData);
			promotionCodeData.setContractTypedata(contractTypedata);
		     final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			 return this.toApiJsonSerializer.serialize(settings, promotionCodeData, RESPONSE_DATA_PARAMETERS);
		}
		
		@PUT
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updatePromotionCode(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePromotionCode(id).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);
		}
		
		 @DELETE
			@Path("{id}")
			@Consumes({MediaType.APPLICATION_JSON})
			@Produces({MediaType.APPLICATION_JSON})
			public String deleteEventAction(@PathParam("id") final Long id) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePromotionCode(id).build();
	     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializer.serialize(result);

			}

}

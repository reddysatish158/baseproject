package org.mifosplatform.billing.promotioncode.api;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.promotioncode.data.PromotionCodeData;
import org.mifosplatform.billing.promotioncode.service.PromotionCodeReadplatformservice;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/promotioncodes")
@Component
@Scope("singleton")
public class PromotionCodeApiResource {
	
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "promotionCode", "promotionDescription", "startDate",
            "durationType", "validUntil", "discountType", "duration","durationType", "discountRate"));
	 
	private final String resourceNameForPermissions = "PROMOTIONCODE";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PromotionCodeReadplatformservice promotionCodeReadplatformservice;
	    
	@Autowired
	public PromotionCodeApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer<PromotionCodeData> toApiJsonSerializer, 
			final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PromotionCodeReadplatformservice promotionCodeReadplatformservice) {
		
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.promotionCodeReadplatformservice=promotionCodeReadplatformservice;
		    }
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllPlans(@Context final UriInfo uriInfo) {
 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<PromotionCodeData> products = this.promotionCodeReadplatformservice.retrieveAllPromotionData();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, products, RESPONSE_DATA_PARAMETERS);
	}

}

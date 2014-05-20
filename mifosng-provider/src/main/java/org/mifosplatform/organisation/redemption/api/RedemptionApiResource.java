package org.mifosplatform.organisation.redemption.api;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/redemption")
@Component
@Scope("singleton")
public class RedemptionApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	/*private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "pinId", "pinNo", "serialNo","clientId"));
	
	private final String resourceNameForPermissions = "REDEMPTION";*/

	//private final PlatformSecurityContext context;
	//private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	
	@Autowired
	public RedemptionApiResource(final PortfolioCommandSourceWritePlatformService writePlatformService){
		//this.context = context;
		//this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.writePlatformService = writePlatformService;
	}
	
	@POST
	//@Path("clientId/{clientId}/{pinNumber}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createRedemption(final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createRedemption().withJson(apiRequestBodyAsJson).build();
		this.writePlatformService.logCommandSource(commandRequest);
		return null;
		
	}
}

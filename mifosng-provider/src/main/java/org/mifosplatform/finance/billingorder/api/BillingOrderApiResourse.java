package org.mifosplatform.finance.billingorder.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.finance.billingorder.service.InvoiceClient;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;


@Path("/billingorder")
@Component
@Scope("singleton")
public class BillingOrderApiResourse {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("systemDate"));
    private final String resourceNameForPermissions = "CODE";

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final BillingOrderReadPlatformService readPlatformService;
    private final InvoiceClient invoiceClient;
    private final FromJsonHelper fromApiJsonHelper;
	@Autowired
	BillingOrderApiResourse(final PlatformSecurityContext context, final BillingOrderReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final InvoiceClient invoiceClient,final FromJsonHelper fromApiJsonHelper){
		
		this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.invoiceClient=invoiceClient;
		this.fromApiJsonHelper=fromApiJsonHelper;
	}
	
	
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveBillingProducts(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
		 final CommandWrapper wrapper = new CommandWrapperBuilder().createInvoice(clientId).withJson(apiRequestBodyAsJson).build();
		 final String json = wrapper.getJson();
			CommandProcessingResult result = null;
			
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);
				final JsonCommand command = JsonCommand.from(json, parsedCommand,
						this.fromApiJsonHelper, wrapper.getEntityName(),
						wrapper.getEntityId(), wrapper.getSubentityId(),
						wrapper.getGroupId(), wrapper.getClientId(),
						wrapper.getLoanId(), wrapper.getSavingsId(),
						wrapper.getCodeId(), wrapper.getSupportedEntityType(),
						wrapper.getSupportedEntityId(), wrapper.getTransactionId());
		this.invoiceClient.createInvoiceBill(command); 
		return this.toApiJsonSerializer.serialize(result);
	}
	
	

}

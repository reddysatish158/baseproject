package org.mifosplatform.organisation.officefinancialtransaction.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.officefinancialtransaction.service.OfficeFinancialTransactionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/officefinancialtransactions")
@Component
@Scope("singleton")
public class OfficeFinancialTransactionApiResource {

	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("transactionId","transactionDate","transactionType","amount","username","officeId"));
	private final PlatformSecurityContext context;
	private final OfficeFinancialTransactionReadPlatformService OfficeFinancialTransactionReadPlatformService;
	private final DefaultToApiJsonSerializer<FinancialTransactionsData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final String resourceNameForPermissions = "officefinancialTransactions";
    @Autowired
    public OfficeFinancialTransactionApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<FinancialTransactionsData> toApiJsonSerializer,
    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
    		final OfficeFinancialTransactionReadPlatformService OfficeFinancialTransactionReadPlatformService){
    	this.apiRequestParameterHelper=apiRequestParameterHelper;
    	this.commandsSourceWritePlatformService=portfolioCommandSourceWritePlatformService;
    	this.context=context;
    	this.toApiJsonSerializer=toApiJsonSerializer;
    	this.OfficeFinancialTransactionReadPlatformService = OfficeFinancialTransactionReadPlatformService;
    }
    
    @GET
    @Path("{officeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTransactionalData(@PathParam("officeId") final Long officeId,@Context final UriInfo uriInfo)	{
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    	final Collection<FinancialTransactionsData> transactionData = this.OfficeFinancialTransactionReadPlatformService.retreiveOfficeFinancialTransactionsData(officeId);
    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, transactionData, RESPONSE_DATA_PARAMETERS);
    }
}

package org.mifosplatform.billing.invoice.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.billing.invoice.service.InvoiceReadPlatformService;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.workflow.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/invoice")
@Component
@Scope("singleton")
public class InvoiceApiResource {
	
	
	  private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "amount","dueAmount","billId","billDate"));
	  private final String resourceNameForPermissions = "INVOICE";
	  private final PlatformSecurityContext context;
	  private final DefaultToApiJsonSerializer<InvoiceData> toApiJsonSerializer;
	
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	
	  private final InvoiceReadPlatformService invoiceReadPlatformService;
	
	    
	    @Autowired
	    public InvoiceApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<InvoiceData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final EventActionMappingReadPlatformService eventActionMappingReadPlatformService,final PlanReadPlatformService planReadPlatformService,
	    		final DefaultToApiJsonSerializer<DiscountMasterData> toApiJsonSerializer1,final MCodeReadPlatformService codeReadPlatformService,
	    		final InvoiceReadPlatformService invoiceReadPlatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService) {

		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.invoiceReadPlatformService =invoiceReadPlatformService;
		        
		    }		
	    
	  
		@GET
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllPromotionDetails(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {
	 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			List<InvoiceData> invoiceDatas= this.invoiceReadPlatformService.retrieveInvoiceDetails(id);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings,invoiceDatas, RESPONSE_DATA_PARAMETERS);
		}
}

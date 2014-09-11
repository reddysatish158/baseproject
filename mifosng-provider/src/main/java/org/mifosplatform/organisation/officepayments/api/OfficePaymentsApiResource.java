package org.mifosplatform.organisation.officepayments.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/officepayments")
@Component
@Scope("singleton")
public class OfficePaymentsApiResource {

	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "officeId", "paymentDate", "paymentCode",
					"amountPaid", "statmentId", "externalId", "Remarks"));
	private final String resourceNameForPermissions = "OFFICEPAYMENT";
	private final PlatformSecurityContext context;
	private final PaymodeReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;

	@Autowired
	public OfficePaymentsApiResource(final PlatformSecurityContext context,final ApiRequestParameterHelper apiRequestParameterHelper,
										final PaymodeReadPlatformService readPlatformService,final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer,
										final PortfolioCommandSourceWritePlatformService writePlatformService){
		 this.context  = context;
		 this.readPlatformService = readPlatformService;
		 this.toApiJsonSerializer = toApiJsonSerializer;
		 this.apiRequestParameterHelper  = apiRequestParameterHelper;
		 this.writePlatformService = writePlatformService;
	}
	
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDetailsForPayments(@QueryParam("officeId") final Long officeId,@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Collection<McodeData> data = this.readPlatformService.retrievemCodeDetails("Payment Mode");
		PaymentData paymentData=new PaymentData(data);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, paymentData,RESPONSE_DATA_PARAMETERS);
	}
	
	@POST
	@Path("{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createOfficePayment(@PathParam("officeId") final Long officeId,final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createOfficePayment(officeId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
}

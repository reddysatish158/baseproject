package org.mifosplatform.billing.paymentsgateway.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.billing.payments.data.PaymentData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/paymentgateways")
@Component
@Scope("singleton")
public class PaymentGatewayApiResource {

	private final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;

	@Autowired
	public PaymentGatewayApiResource(
			final DefaultToApiJsonSerializer<PaymentData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService writePlatformService) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.writePlatformService = writePlatformService;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String mpesaPayment(final String apiRequestBodyAsJson) {
		try {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentGateway().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		} catch (Exception e) {
			return null;
		}
	}

}

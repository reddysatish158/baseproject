package org.mifosplatform.billing.paymentsgateway.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.XML;
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
	@Consumes({ MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_XML })
	public String mpesaPayment(final String apiRequestBodyAsJson) {
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(apiRequestBodyAsJson);
			JSONObject element= xmlJSONObj.getJSONObject("transaction");
			element.put("locale", "en");
            System.out.println(element);
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentGateway().withJson(element.toString()).build();
			final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
			
			if("Success".equalsIgnoreCase(result.getTransactionId())){
            StringBuilder builder = new StringBuilder();
            builder
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
                .append("<response>")
                .append("<receipt>"+result.resourceId())
                .append("</receipt>")
                 .append("<result>"+result.getTransactionId())
                .append("</result>")
                .append("</response>");
            return builder.toString();
			}
			else{
				StringBuilder failurebuilder = new StringBuilder();
				failurebuilder
	                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
	                .append("<response>")
	                .append("<receipt/>")
	                 .append("<result>"+"FAILURE")
	                .append("</result>")
	                .append("</response>");
	            return failurebuilder.toString();
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}

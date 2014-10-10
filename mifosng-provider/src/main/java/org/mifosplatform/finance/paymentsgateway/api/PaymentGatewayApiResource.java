
package org.mifosplatform.finance.paymentsgateway.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayReadPlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/paymentgateways")
@Component
@Scope("singleton")
public class PaymentGatewayApiResource {
	
	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id","paymentId", "serialNo", "paymentDate", "receiptNo","status","phoneNo","clientName","amountPaid","remarks"));
	
	private final String resourceNameForPermissions = "PAYMENTGATEWAY";

	private final PlatformSecurityContext context;
	private final PaymentGatewayReadPlatformService readPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<PaymentGatewayData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService;

	@Autowired
	public PaymentGatewayApiResource(final PlatformSecurityContext context,final PaymentGatewayReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<PaymentGatewayData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService writePlatformService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.writePlatformService = writePlatformService;
		this.context=context;
		this.readPlatformService=readPlatformService;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.paymentGatewayReadPlatformService=paymentGatewayReadPlatformService;
	}

	@POST
	@Consumes({ MediaType.WILDCARD })
	@Produces({ MediaType.APPLICATION_XML })
	public String mpesaPayment(final String apiRequestBodyAsJson)  {
		
		 CommandProcessingResult result=null;
		 JSONObject jsonData = null;
	     try{
			JSONObject xmlJSONObj = XML.toJSONObject(apiRequestBodyAsJson);
			jsonData=this.ReturnJsonFromXml(xmlJSONObj);
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentGateway().withJson(jsonData.toString()).build();
			result = this.writePlatformService.logCommandSource(commandRequest);
			String Success="SUCCESS";
			String errorDesc="";
			Long errorCode=0L;
			String contentData="OBSTRANSACTIONID="+result.resourceId();
			return this.returnToStalker(jsonData,Success,errorDesc,errorCode,contentData);
			
		}catch(ReceiptNoDuplicateException e){
				String Success="DUPLICATE_TXN";
				String errorDesc="DUPLICATE";
				Long errorCode=1L;
				String contentData="TXNID ALREADY EXIST";
				return this.returnToStalker(jsonData,Success,errorDesc,errorCode,contentData);
		} catch (JSONException e) {
			    return e.getCause().toString();	 
		} catch (PlatformDataIntegrityException e) {
		        return null;
	    }   
	}

	private String returnToStalker(JSONObject jsonData, String success, String errorDesc, Long errorCode, String contentData) {
		
		try {
			String OBSPAYMENTTYPE = jsonData.getString("OBSPAYMENTTYPE");
		
			String returnMessage = null;
			if(OBSPAYMENTTYPE.equalsIgnoreCase("MPesa")){
				
					     String Receipt=jsonData.getString("receipt");
						 StringBuilder builder = new StringBuilder();
				            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
				                .append("<response>")
				                .append("<receipt>"+Receipt)
				                .append("</receipt>")
				                .append("<result>"+success)
				                .append("</result>")
				                .append("</response>");
				            returnMessage= builder.toString();
					
			}else if (OBSPAYMENTTYPE.equalsIgnoreCase("TigoPesa")) {
				
					String TYPE = jsonData.getString("TYPE");
					String TXNID = jsonData.getString("TXNID");
					String CUSTOMERREFERENCEID = jsonData.getString("CUSTOMERREFERENCEID");	
					String MSISDN = jsonData.getString("MSISDN");
					
						 StringBuilder builder = new StringBuilder();
				            builder.append("<?xml version=\"1.0\"?>")
				                .append("<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">")
				                .append("<COMMAND>")
				                .append("<TYPE>"+TYPE)
				                .append("</TYPE>")
				                .append("<TXNID>"+TXNID)
				                .append("</TXNID>")
				                .append("<REFID>"+CUSTOMERREFERENCEID)
				                .append("</REFID>")
				                .append("<RESULT>"+success)
				                .append("</RESULT>")
				                .append("<ERRORCODE>"+errorCode)
				                .append("</ERRORCODE>")
				                .append("<ERRORDESC>"+errorDesc)
				                .append("</ERRORDESC>")
				                .append("<MSISDN>"+MSISDN)
				                .append("</MSISDN>")
				                .append("<FLAG>"+"Y")
				                .append("</FLAG>")
				                .append("<CONTENT>"+contentData)
				                .append("</CONTENT>")
				                .append("</COMMAND>");
				            
				            returnMessage= builder.toString();			 
					
		}
		return returnMessage;
		} catch (JSONException e) {
			return e.getCause().toString();	 
		}
		
	}

	public JSONObject ReturnJsonFromXml(JSONObject xmlJSONObj){		
		try {
			JSONObject element=null;
			boolean b=xmlJSONObj.has("COMMAND");
			
			if(b==true){
			    element = xmlJSONObj.getJSONObject("COMMAND");
			    element.put("OBSPAYMENTTYPE", "TigoPesa");
			    element.put("locale", "en");
			}else{
				element = xmlJSONObj.getJSONObject("transaction");
				element.put("OBSPAYMENTTYPE", "MPesa");
				element.put("locale", "en");
			}
			return element;
		} catch (JSONException e) { 
			return null;
		}
		
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllDetailsForPayments(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,@QueryParam("tabType") final String type) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		Page<PaymentGatewayData> paymentData = readPlatformService.retrievePaymentGatewayData(searchItemDetails,type);
		return this.toApiJsonSerializer.serialize(paymentData);

	}
	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllDetailsForPayments(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		PaymentGatewayData paymentData = readPlatformService.retrievePaymentGatewayIdData(id);
		List<MediaEnumoptionData> data=readPlatformService.retrieveTemplateData();
		paymentData.setStatusData(data);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, paymentData,RESPONSE_DATA_PARAMETERS);

	}
	
	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateData(@PathParam("id") final Long id,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaymentGateway(id).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		 return this.toApiJsonSerializer.serialize(result);

	}

}


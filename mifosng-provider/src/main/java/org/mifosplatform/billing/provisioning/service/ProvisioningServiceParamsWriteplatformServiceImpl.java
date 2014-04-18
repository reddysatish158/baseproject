package org.mifosplatform.billing.provisioning.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderLine;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.preparerequest.domain.PrepareRequest;
import org.mifosplatform.billing.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.billing.provisioning.domain.ServiceParameters;
import org.mifosplatform.billing.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.billing.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class ProvisioningServiceParamsWriteplatformServiceImpl implements ProvisioningServiceParamsWriteplatformService{
	
	
	private final PlatformSecurityContext context;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ServiceParametersRepository serviceParametersRepository;
    private final PrepareRequsetRepository prepareRequsetRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final OrderRepository orderRepository;
    private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
@Autowired	
public ProvisioningServiceParamsWriteplatformServiceImpl(final PlatformSecurityContext securityContext,final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,
		final FromJsonHelper fromJsonHelper,final ServiceParametersRepository parametersRepository,final PrepareRequsetRepository prepareRequsetRepository,
		final ProcessRequestRepository processRequestRepository,final OrderRepository orderRepository,final InventoryItemDetailsRepository detailsRepository){
	
	this.context=securityContext;
	this.fromApiJsonDeserializer=fromApiJsonDeserializer;
	this.fromApiJsonHelper=fromJsonHelper;
	this.serviceParametersRepository=parametersRepository;
	this.prepareRequsetRepository=prepareRequsetRepository;
	this.processRequestRepository=processRequestRepository;
	this.orderRepository=orderRepository;
	this.inventoryItemDetailsRepository=detailsRepository;
	
}

	@Transactional
	@Override
	public CommandProcessingResult updateServiceParams(JsonCommand command,Long orderId) {
	
		try{
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForAddProvisioning(command.json());
			 final JsonElement element = fromApiJsonHelper.parse(command.json());
				JsonArray serviceParameters = fromApiJsonHelper.extractJsonArrayNamed("serviceParameters", element);
				
				JSONObject jsonObject=new JSONObject();
			List<ServiceParameters> parameters=this.serviceParametersRepository.findDataByOrderId(orderId);
			

	   /*     for(JsonElement j:serviceParameters){
	        	
	        	ServiceParameters serviceParameter=ServiceParameters.fromJson(j,fromApiJsonHelper,clientId,orderId,planName);
				//this.serviceParametersRepository.saveAndFlush(serviceParameter);1
				//jsonObject.put(serviceParameter.getParameterName(),serviceParameter.getParameterValue());
	        }
	        }*/
			
			for(ServiceParameters serviceParameter:parameters){
				
				String oldValue=serviceParameter.getParameterValue();
				Map<String, Object>  changes=serviceParameter.updateServiceParam(serviceParameters,fromApiJsonHelper);
				
				this.serviceParametersRepository.saveAndFlush(serviceParameter);
				
                 if(!changes.isEmpty()){
					
					jsonObject.put("OLD_"+serviceParameter.getParameterName(), oldValue);
					jsonObject.put(serviceParameter.getParameterName(), serviceParameter.getParameterValue());
				}
				
			}
			Order order=this.orderRepository.findOne(orderId);
			  PrepareRequest prepareRequest=this.prepareRequsetRepository.getLatestRequestByOrderId(orderId);
				InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(command.stringValueOfParameterNamed("macId"));
			
			ProcessRequest processRequest=new ProcessRequest(order.getClientId(),orderId,ProvisioningApiConstants.PROV_PACKETSPAN, 'N',
					null,"CHANGE_PROVISIONING", prepareRequest.getId());
			
			
			List<OrderLine> orderLines=order.getServices();
			
			for(OrderLine orderLine:orderLines){
				 
				ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),jsonObject.toString(),"Recieved",
						inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),order.getEndDate(),null,null,'N',"CHANGE_PROVISIONING");
				  processRequest.add(processRequestDetails);
				
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);
			
			return new CommandProcessingResult(Long.valueOf(orderId));
			
		}catch(DataIntegrityViolationException dataIntegrityViolationException){
			handleCodeDataIntegrityIssues(command, dataIntegrityViolationException);
			return new CommandProcessingResult(Long.valueOf(-1l));
		}
		
		
		
	}
	
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
        throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
	}
	
}

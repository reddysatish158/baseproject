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
import org.mifosplatform.billing.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.billing.provisioning.domain.ServiceParameters;
import org.mifosplatform.billing.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.billing.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
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
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {

	

	private final PlatformSecurityContext context;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ProvisioningCommandRepository provisioningCommandRepository;
    private final ServiceParametersRepository serviceParametersRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final OrderRepository orderRepository;
    private final PrepareRequestReadplatformService prepareRequestReadplatformService;
    private final PrepareRequsetRepository prepareRequsetRepository;
	private final FromJsonHelper fromJsonHelper;
	private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
    
    @Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,final InventoryItemDetailsRepository inventoryItemDetailsRepository,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,
			final ProvisioningCommandRepository provisioningCommandRepository,final ServiceParametersRepository parametersRepository,
			final ProcessRequestRepository processRequestRepository,final OrderRepository orderRepository,final PrepareRequsetRepository prepareRequsetRepository,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,final FromJsonHelper fromJsonHelper) {
		
		this.context = context;		
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.provisioningCommandRepository=provisioningCommandRepository;
		this.serviceParametersRepository=parametersRepository;
		this.processRequestRepository=processRequestRepository;
		this.orderRepository=orderRepository;
		this.prepareRequestReadplatformService=prepareRequestReadplatformService;
		this.prepareRequsetRepository=prepareRequsetRepository;
		this.fromJsonHelper=fromJsonHelper;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		

	}

	@Override
	public CommandProcessingResult createProvisioning(JsonCommand command) {
		
			try{	
				 this.context.authenticatedUser();
				 this.fromApiJsonDeserializer.validateForProvisioning(command.json());
				 ProvisioningCommand provisioningCommand=ProvisioningCommand.from(command);
				 
				 final JsonElement element = fromApiJsonHelper.parse(command.json());
				 final JsonArray commandArray=fromApiJsonHelper.extractJsonArrayNamed("commandParameters",element);
				 if(commandArray!=null){
			     for (JsonElement jsonelement : commandArray) {	
				          String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);		    
				          String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);	
				          String paramDefault=null;
				          if(fromApiJsonHelper.parameterExists("paramDefault", jsonelement)){
				        	  paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);	
				          }
				          ProvisioningCommandParameters data=new ProvisioningCommandParameters(commandParam,paramType,paramDefault);
				          provisioningCommand.addCommandParameters(data);
			     }
			     }
			     
			     this.provisioningCommandRepository.save(provisioningCommand);
			     
			     return new CommandProcessingResult(provisioningCommand.getId());	
			     
			}catch (DataIntegrityViolationException dve) {
				handleCodeDataIntegrityIssues(command, dve);
				return new CommandProcessingResult(Long.valueOf(-1));
			}
			
		
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
	}

	@Override
	public CommandProcessingResult updateProvisioning(JsonCommand command) {

		try{	
			 this.context.authenticatedUser();
			 this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			 ProvisioningCommand provisioningCommand= this.provisioningCommandRepository.findOne(command.entityId());
			 provisioningCommand.getCommandparameters().clear();
			 
			 final Map<String, Object> changes = provisioningCommand.UpdateProvisioning(command);
			 
			 final JsonElement element = fromApiJsonHelper.parse(command.json());
			 final JsonArray commandArray=fromApiJsonHelper.extractJsonArrayNamed("commandParameters",element);
			 if(commandArray!=null){
		     for (JsonElement jsonelement : commandArray) {	
			          String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);		    
			          String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);	
			          String paramDefault=null;
			          if(fromApiJsonHelper.parameterExists("paramDefault", jsonelement)){
			        	  paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);	
			          }
			          ProvisioningCommandParameters data=new ProvisioningCommandParameters(commandParam,paramType,paramDefault);
			          provisioningCommand.addCommandParameters(data);
		     }
		     }
		     
		     this.provisioningCommandRepository.save(provisioningCommand);
		     
		     return new CommandProcessingResult(provisioningCommand.getId());	
		     
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult deleteProvisioningSystem(JsonCommand command) {
		try{	
			 this.context.authenticatedUser();
			 ProvisioningCommand provisioningCommand= this.provisioningCommandRepository.findOne(command.entityId());
			 if(provisioningCommand.getIsDeleted()!='Y')
			 {
				 provisioningCommand.setIsDeleted('Y');
			 }
		     this.provisioningCommandRepository.save(provisioningCommand);
		     
		     return new CommandProcessingResult(provisioningCommand.getId());	
		     
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult createNewProvisioningSystem(JsonCommand command, Long entityId) {
		
		try{
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForAddProvisioning(command.json());
            final Long orderId=command.longValueOfParameterNamed("orderId");
            final Long clientId=command.longValueOfParameterNamed("clientId");
            final String planName=command.stringValueOfParameterNamed("planName");
            final String macId=command.stringValueOfParameterNamed("macId");
			
			//Integer prepareId=this.prepareRequestReadplatformService.getLastPrepareId(orderId);
            PrepareRequest prepareRequest=this.prepareRequsetRepository.getLatestRequestByOrderId(orderId);
			InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(macId);
			
			 final JsonElement element = fromJsonHelper.parse(command.json());
			JsonArray serviceParameters = fromJsonHelper.extractJsonArrayNamed("serviceParameters", element);
			
			JSONObject jsonObject=new JSONObject();

	        for(JsonElement j:serviceParameters){
	        	
				ServiceParameters serviceParameter=ServiceParameters.fromJson(j,fromJsonHelper,clientId,orderId,planName);
				this.serviceParametersRepository.saveAndFlush(serviceParameter);
				jsonObject.put(serviceParameter.getParameterName(),serviceParameter.getParameterValue());
	        }
			
	        jsonObject.put("clientId",clientId);
	        jsonObject.put("orderId",orderId);
	        jsonObject.put("planName",planName);
	        jsonObject.put("macId",macId);
	        
			ProcessRequest processRequest=new ProcessRequest(clientId,orderId,ProvisioningApiConstants.PROV_PACKETSPAN, 'N',
					null,UserActionStatusTypeEnum.ACTIVATION.toString(), prepareRequest.getId());
			
			Order order=this.orderRepository.findOne(orderId);
			List<OrderLine> orderLines=order.getServices();
			
			for(OrderLine orderLine:orderLines){
				 
				ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),jsonObject.toString(),"Recieved",
						inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),order.getEndDate(),null,null,'N',UserActionStatusTypeEnum.ACTIVATION.toString());
				  processRequest.add(processRequestDetails);
				
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);

			//Update Prepare Request table
			
				
			//PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(prepareId.longValue());
			prepareRequest.updateProvisioning();
			this.prepareRequsetRepository.save(prepareRequest);
			
			//}
			return new CommandProcessingResult(Long.valueOf(processRequest.getId()));
			
			
			
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			
		}
		
	
	}
	
}

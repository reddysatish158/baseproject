package org.mifosplatform.provisioning.provisioning.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetails;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpAddresAllocatedException;
import org.mifosplatform.organisation.ippool.exception.IpNotAvailableException;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.exception.PairingNotExistException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.order.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.provisioning.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {

	

	
	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
    private final OrderRepository orderRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final ClientRepository clientRepository;
    private final ServiceMasterRepository serviceMasterRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final PrepareRequsetRepository prepareRequsetRepository;
    private final OrderReadPlatformService orderReadPlatformService;
    private final HardwareAssociationRepository associationRepository;
    private final ServiceParametersRepository serviceParametersRepository;
    private final ProvisioningCommandRepository provisioningCommandRepository;
    private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
    private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ProcessRequestReadplatformService processRequestReadplatformService;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	
    @Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,final InventoryItemDetailsRepository inventoryItemDetailsRepository,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,final OrderReadPlatformService orderReadPlatformService,
			final ProvisioningCommandRepository provisioningCommandRepository,final ServiceParametersRepository parametersRepository,
			final ProcessRequestRepository processRequestRepository,final OrderRepository orderRepository,final PrepareRequsetRepository prepareRequsetRepository,
			final FromJsonHelper fromJsonHelper,final HardwareAssociationRepository associationRepository,final ServiceMasterRepository serviceMasterRepository,
			final ProcessRequestReadplatformService processRequestReadplatformService,final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,
			final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService,final ClientRepository clientRepository) {

    	this.context = context;
    	this.fromJsonHelper=fromJsonHelper;
		this.orderRepository=orderRepository;
		this.clientRepository=clientRepository;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.associationRepository=associationRepository;
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.serviceMasterRepository=serviceMasterRepository;
		this.serviceParametersRepository=parametersRepository;
		this.processRequestRepository=processRequestRepository;
		this.prepareRequsetRepository=prepareRequsetRepository;
		this.orderReadPlatformService=orderReadPlatformService;
		this.provisioningCommandRepository=provisioningCommandRepository;
		this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		this.processRequestReadplatformService=processRequestReadplatformService;
		this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;

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
            final String ipType=command.stringValueOfParameterNamed("ipType");
            final String iprange=command.stringValueOfParameterNamed("ipRange");
            final Long subnet=command.longValueOfParameterNamed("subnet");
            String[] ipAddressArray =null;
            PrepareRequest prepareRequest=this.prepareRequsetRepository.getLatestRequestByOrderId(orderId);
			InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(macId);
			
			if(inventoryItemDetails == null){
				throw new PairingNotExistException(orderId);
			}
			
			 final JsonElement element = fromJsonHelper.parse(command.json());
			 JsonArray serviceParameters = fromJsonHelper.extractJsonArrayNamed("serviceParameters", element);
			
			JSONObject jsonObject=new JSONObject();
	        for(JsonElement j:serviceParameters){
	        	
				ServiceParameters serviceParameter=ServiceParameters.fromJson(j,fromJsonHelper,clientId,orderId,planName,"ACTIVE",iprange,subnet);
				this.serviceParametersRepository.saveAndFlush(serviceParameter);
				
				//ip_pool_data status updation
				String paramName = fromJsonHelper.extractStringNamed("paramName", j);
				 if(paramName.equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_IPADDRESS)){
					 
					  if(iprange.equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_SUBNET)){
					      
						  String ipAddress=fromJsonHelper.extractStringNamed("paramValue",j);
						  String ipData=ipAddress+"/"+subnet;
						  IpGeneration ipGeneration=new IpGeneration(ipData,this.ipPoolManagementReadPlatformService);
						  ipAddressArray=ipGeneration.getInfo().getsubnetAddresses();
							
							for(int i=0;i<ipAddressArray.length;i++){
								
								IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findIpAddressData(ipAddressArray[i]);
								if(ipPoolManagementDetail == null){
									throw new IpAddresAllocatedException(ipAddressArray[i]);
								}
							}
							
							jsonObject.put(ProvisioningApiConstants.PROV_DATA_SUBNET,subnet);
					 }else{
					 ipAddressArray = fromJsonHelper.extractArrayNamed("paramValue", j);
					 }

					  for(String ipAddress:ipAddressArray){
						IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findIpAddressData(ipAddress);

						if(ipPoolManagementDetail == null){
								throw new IpNotAvailableException(ipAddress);
						}
						ipPoolManagementDetail.setStatus('A');
						ipPoolManagementDetail.setClientId(clientId);
						this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
						
					}
				}
				jsonObject.put(serviceParameter.getParameterName(),serviceParameter.getParameterValue());
				
			    }
	        
			Client client=this.clientRepository.findOne(clientId);
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTID,client.getAccountNo());
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTNAME,client.getFirstname());
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_ORDERID,orderId);
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_PLANNAME,planName);
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_MACID,macId);
	        jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE,ipType);
	        
			ProcessRequest processRequest=new ProcessRequest(clientId,orderId,ProvisioningApiConstants.PROV_PACKETSPAN, 'N',
					null,UserActionStatusTypeEnum.ACTIVATION.toString(), prepareRequest.getId());
			
			Order order=this.orderRepository.findOne(orderId);
			List<OrderLine> orderLines=order.getServices();
			
			for(OrderLine orderLine:orderLines){
				
				ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE,service.getServiceType());
				ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),
						jsonObject.toString(),"Recieved",inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),
						order.getEndDate(),null,null,'N',UserActionStatusTypeEnum.ACTIVATION.toString());
				  processRequest.add(processRequestDetails);
				
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);
			//Update Prepare Request table
			prepareRequest.updateProvisioning('Y');
			this.prepareRequsetRepository.save(prepareRequest);
			return new CommandProcessingResult(Long.valueOf(processRequest.getId()));
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			
		}
		
	
	}

    @Transactional
	@Override
	public CommandProcessingResult updateProvisioningDetails(Long entityId) {
		
	   try{
			this.context.authenticatedUser();
			ProcessRequest processRequest=this.processRequestRepository.findOne(entityId);
			/*List<ProcessRequestDetails> details=processRequest.getProcessRequestDetails();
			
			GlobalConfigurationProperty configurationProperty=this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
			if(!details.isEmpty()){
			String oldHardWare=details.get(0).getHardwareId();
			final String newHardwareId=command.stringValueOfParameterNamed("hardwareId");
			
			  if (!oldHardWare.equalsIgnoreCase(newHardwareId)) {
		        Long  id=this.provisioningReadPlatformService.getHardwareDetails(oldHardWare,processRequest.getClientId(),configurationProperty.getValue());
		        
		        if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
		        	
		        	OwnedHardware ownedHardware=this.hardwareJpaRepository.findOne(id);
		        	
		        	ownedHardware.updateSerialNumbers(newHardwareId);
		        	this.hardwareJpaRepository.saveAndFlush(ownedHardware);
		        	
		        }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
		        	
		        	InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.findOne(id);
		        	inventoryItemDetails.setProvisioningSerialNumber(newHardwareId);
		        	this.inventoryItemDetailsRepository.saveAndFlush(inventoryItemDetails);
		        }
				    				  
				  
		        }
			}
                  
			for(ProcessRequestDetails processRequestDetails:details){
				processRequestDetails.updateDetails(command);
			}*/
			if(processRequest != null){
			processRequest.update();
			this.processRequestRepository.saveAndFlush(processRequest);
			}
			
		return new CommandProcessingResult(entityId);	
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}

    @Transactional
	@Override
	public void updateHardwareDetails(Long clientId, String serialNumber,String oldSerialnumber,String provSerilaNum,String oldHardware) {
		
		Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,oldSerialnumber);
	    if(activeorders!= 0){
		   throw new ActivePlansFoundException(serialNumber);
	   }
	   
	  //Update in Association table if Exist
	   HardwareAssociation hardwareAssociation=this.associationRepository.findOneByserialNo(oldSerialnumber);
	   if(hardwareAssociation != null){
	   hardwareAssociation.updateserailNum(serialNumber);
	   this.associationRepository.saveAndFlush(hardwareAssociation);
	   }
	   
	 //Update ProcessRequest
	   final Long ProcessReqId=this.processRequestReadplatformService.retrievelatestReqId(clientId,oldHardware);
	   
	   if(ProcessReqId != null && !ProcessReqId.equals(new Long(0))){
		   
		   ProcessRequest processRequest=this.processRequestRepository.findOne(ProcessReqId);
		   
		   List<ProcessRequestDetails> processRequestDetails=processRequest.getProcessRequestDetails();
		   
		   for(ProcessRequestDetails details:processRequestDetails){
			   details.update(provSerilaNum);
		   }
		   this.processRequestRepository.saveAndFlush(processRequest);
	   }
	}
	@Transactional
    @Override
	public void postOrderDetailsForProvisioning(Order order,String planName,String requestType,Long prepareId,String groupname) {
		try{
			
			this.context.authenticatedUser();
			List<ServiceParameters> parameters=this.serviceParametersRepository.findDataByOrderId(order.getId());
			
			if(!parameters.isEmpty()){
			    ProcessRequest processRequest=new ProcessRequest(prepareId,order.getClientId(),order.getId(),ProvisioningApiConstants.PROV_PACKETSPAN, requestType);
			    List<OrderLine> orderLines=order.getServices();
			    HardwareAssociation hardwareAssociation=this.associationRepository.findOneByOrderId(order.getId());
			    if(hardwareAssociation == null){
			    	throw new PairingNotExistException(order.getId());
			    }
			    InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(hardwareAssociation.getSerialNo());
			    
			    if(inventoryItemDetails == null){
			    	throw new PairingNotExistException(order.getId());
			    }
			    
			    Client client=this.clientRepository.findOne(order.getClientId());
			    JSONObject jsonObject=new JSONObject();
			    jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTID,client.getAccountNo());
			    jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTNAME,client.getFirstname());
		        jsonObject.put(ProvisioningApiConstants.PROV_DATA_ORDERID,order.getId());
		        jsonObject.put(ProvisioningApiConstants.PROV_DATA_PLANNAME,planName);
		        jsonObject.put(ProvisioningApiConstants.PROV_DATA_MACID,inventoryItemDetails.getSerialNumber());
		        if(groupname != null){
		        	jsonObject.put(ProvisioningApiConstants.PROV_DATA_OLD_GROUPNAME,groupname);
		        }
		        for(ServiceParameters serviceParameters:parameters){
		        	
		        	if(serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_IPADDRESS)){
		        		if(serviceParameters.getParameterValue().contains("/")){
		        			jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE,"Subnet");
		        		}else if(serviceParameters.getParameterValue().contains("[")){
		        			JSONArray jsonArray=new JSONArray(serviceParameters.getParameterValue());
		        			if(jsonArray.length() > 1)
		        			jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE,"Multiple");
		        		}else{
		        			jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE,"Single");
		        		}
		        	}
		        	if(serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_GROUPNAME) && groupname != null){
		        		jsonObject.put("NEW_"+serviceParameters.getParameterName(),serviceParameters.getParameterValue());
		        	}else{
		        	   jsonObject.put(serviceParameters.getParameterName(),serviceParameters.getParameterValue());
		        	}
		        	
		        }
			    for(OrderLine orderLine:orderLines){
			    	
			    	
					ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
					jsonObject.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE,service.getServiceType());
			    	
			    	 ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),
								jsonObject.toString(),"Recieved",inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),
								order.getEndDate(),null,null,'N',requestType);
						  processRequest.add(processRequestDetails);
				
			     }

			this.processRequestRepository.save(processRequest);
			}
			
		}catch(DataIntegrityViolationException dve){
			
			handleCodeDataIntegrityIssues(null, dve);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}

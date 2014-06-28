package org.mifosplatform.provisioning.provisioning.service;

import java.util.List;

import net.sf.json.JSONObject;

import org.codehaus.jettison.json.JSONArray;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpAddresAllocatedException;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
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
public class ProvisioningServiceParamsWriteplatformServiceImpl implements ProvisioningServiceParamsWriteplatformService{
	
	
	private final PlatformSecurityContext context;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ServiceParametersRepository serviceParametersRepository;
    private final PrepareRequsetRepository prepareRequsetRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final OrderRepository orderRepository;
    private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
    private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
    private final ServiceMasterRepository serviceMasterRepository;
    private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
  
@Autowired	
public ProvisioningServiceParamsWriteplatformServiceImpl(final PlatformSecurityContext securityContext,final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,
		final FromJsonHelper fromJsonHelper,final ServiceParametersRepository parametersRepository,final PrepareRequsetRepository prepareRequsetRepository,
		final ProcessRequestRepository processRequestRepository,final OrderRepository orderRepository,final InventoryItemDetailsRepository detailsRepository,
		final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,final ServiceMasterRepository masterRepository,
		final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService){
	
	this.context=securityContext;
	this.fromApiJsonDeserializer=fromApiJsonDeserializer;
	this.fromApiJsonHelper=fromJsonHelper;
	this.serviceParametersRepository=parametersRepository;
	this.prepareRequsetRepository=prepareRequsetRepository;
	this.processRequestRepository=processRequestRepository;
	this.orderRepository=orderRepository;
	this.inventoryItemDetailsRepository=detailsRepository;
	this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
	this.serviceMasterRepository=masterRepository;
	this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
}

	@Transactional
	@Override
	public CommandProcessingResult updateServiceParams(JsonCommand command,Long orderId) {
	
		try{
			
			     this.context.authenticatedUser();
			     this.fromApiJsonDeserializer.validateForAddProvisioning(command.json());
			     final JsonElement element = fromApiJsonHelper.parse(command.json());
				 JsonArray serviceParameters = fromApiJsonHelper.extractJsonArrayNamed("serviceParameters", element);
				 String[] ipAddressArray=null;
				 JSONObject jsonObject=new JSONObject();
				 JSONArray oldIpAddressArray=null;
			List<ServiceParameters> parameters=this.serviceParametersRepository.findDataByOrderId(orderId);
			//
	            final Long clientId=command.longValueOfParameterNamed("clientId");
	            final String planName=command.stringValueOfParameterNamed("planName");
	            final String macId=command.stringValueOfParameterNamed("macId");
	            final String ipType=command.stringValueOfParameterNamed("ipType");
	            final String ipRange=command.stringValueOfParameterNamed("ipRange");
	            final Long subnet=command.longValueOfParameterNamed("subnet");
				jsonObject.put("clientId", clientId);
				jsonObject.put("orderId", orderId);
				jsonObject.put("macId", macId);
				jsonObject.put("planName",planName);

				for(ServiceParameters serviceParameter:parameters){
				
				String oldValue=serviceParameter.getParameterValue();
				for(JsonElement jsonElement:serviceParameters){
					
				String paramName=fromApiJsonHelper.extractStringNamed("paramName",jsonElement);
				String service=fromApiJsonHelper.extractStringNamed("paramValue", jsonElement);
					   
					if(serviceParameter.getParameterName().equalsIgnoreCase(paramName)){
						
						if(!oldValue.equalsIgnoreCase(service)){

							serviceParameter.setStatus("INACTIVE");
				            this.serviceParametersRepository.saveAndFlush(serviceParameter);
						    serviceParameter=ServiceParameters.fromJson(jsonElement,fromApiJsonHelper,clientId,orderId,planName,"ACTIVE",ipRange,subnet);
						    this.serviceParametersRepository.saveAndFlush(serviceParameter);
                            
						    if(serviceParameter.getParameterName().equalsIgnoreCase("IP_ADDRESS")){
                     		  
						    	if(ipRange.equalsIgnoreCase("subnet")){
                    	           
						    		String ipAddress=fromApiJsonHelper.extractStringNamed("paramValue",jsonElement);
 						            String ipData=ipAddress+"/"+subnet;
 					      	      IpGeneration ipGeneration=new IpGeneration(ipData,this.ipPoolManagementReadPlatformService);
 					      	     //   ipAddressArray=this.ipGeneration.getInfo().getAllAddresses(ipData);//
 					      	   ipAddressArray= ipGeneration.getInfo().getsubnetAddresses();
 						            
 					      	         for(int i=0;i<ipAddressArray.length;i++){
								         IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findIpAddressData(ipAddressArray[i]);
  								         
								         if(ipPoolManagementDetail == null){
									     throw new IpAddresAllocatedException(ipAddressArray[i]);
								         }
 							         }
 							            jsonObject.put("subnet",subnet);
                    		   }else{
                    			 ipAddressArray=fromApiJsonHelper.extractArrayNamed("paramValue", jsonElement);//new  JSONArray(ipAddressArray);
                    		    }
						      
							     for(String ipaddress:ipAddressArray){
							      IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findIpAddressData(ipaddress);
							      ipPoolManagementDetail.setStatus('A');
							      ipPoolManagementDetail.setClientId(clientId);
							      this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
						      }
							     jsonObject.put("new_ip_type", ipType);
							    
							     if(oldValue.contains("/")){
								    IpGeneration ipGeneration=new IpGeneration(oldValue,this.ipPoolManagementReadPlatformService);
		 					        //ipAddressArray=this.ipGeneration.getInfo().getsubnetAddresses(oldValue);
		 					          
		 					        for(int i=0;i<ipAddressArray.length;i++){
  		 					    	    IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findAllocatedIpAddressData(ipAddressArray[i]);
									  
		 					    	  if(ipPoolManagementDetail != null){
									         ipPoolManagementDetail.setStatus('F');
									         ipPoolManagementDetail.setClientId(null);
									         this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
									     }
								    }
		 					          jsonObject.put("old_ip_type","Subnet");
		 					      
								}else{
									
                  	            oldIpAddressArray=new  JSONArray(oldValue);
                  	            for(int i=0;i<oldIpAddressArray.length();i++){
							      IpPoolManagementDetail ipPoolManagementDetail= this.ipPoolManagementJpaRepository.findAllocatedIpAddressData(oldIpAddressArray.getString(i));
							     
							      if(ipPoolManagementDetail != null){
							        ipPoolManagementDetail.setStatus('F');
							        ipPoolManagementDetail.setClientId(null);
							        this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
							      }
						       }
                  	      	       if(oldIpAddressArray.length() >1){
						  		   jsonObject.put("old_ip_type","multiple");
						  	        
                  	      	       }else{
						  		   jsonObject.put("old_ip_type","single");
						  	         }
								}
						
					   jsonObject.put("OLD_"+serviceParameter.getParameterName(), oldValue);
					   jsonObject.put("NEW_"+serviceParameter.getParameterName(), serviceParameter.getParameterValue());
					
					
					}
				}else{
					jsonObject.put(serviceParameter.getParameterName(), serviceParameter.getParameterValue());
				}
				
			}
	      }
	    }
			  Order order=this.orderRepository.findOne(orderId);
			  PrepareRequest prepareRequest=this.prepareRequsetRepository.getLatestRequestByOrderId(orderId);
			  InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(command.stringValueOfParameterNamed("macId"));
  			  ProcessRequest processRequest=new ProcessRequest(order.getClientId(),orderId,ProvisioningApiConstants.PROV_PACKETSPAN, 'N',
					null,"CHANGE_PROVISIONING", prepareRequest.getId());
			  
  			  List<OrderLine> orderLines=order.getServices();
			  
  			  for(OrderLine orderLine:orderLines){
				 ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
				 jsonObject.put("service_type",service.getServiceType());
				ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),jsonObject.toString(),"Recieved",
						inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),order.getEndDate(),null,null,'N',"CHANGE_PROVISIONING");
				  processRequest.add(processRequestDetails);
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);
			return new CommandProcessingResult(Long.valueOf(orderId));
			
		}catch(DataIntegrityViolationException dataIntegrityViolationException){
		
			handleCodeDataIntegrityIssues(command, dataIntegrityViolationException);
			return new CommandProcessingResult(Long.valueOf(-1l));
		
		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1l));
		}
	}
	
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
        throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
	}
	
}

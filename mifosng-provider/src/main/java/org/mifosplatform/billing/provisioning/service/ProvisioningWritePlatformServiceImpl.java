package org.mifosplatform.billing.provisioning.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderLine;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.domain.ServiceParametersRepository;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.billing.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.billing.provisioning.domain.ServiceParameters;
import org.mifosplatform.billing.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetails;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetailsRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {

	

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final FromJsonHelper fromApiJsonHelper;
    private final ProvisioningCommandRepository provisioningCommandRepository;
    private final ServiceParametersRepository serviceParametersRepository;
    private final ProcessRequestRepository processRequestRepository;
    private final OrderRepository orderRepository;
    private final ProvisionServiceDetailsRepository provisionServiceDetailsRepository;
    
    @Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,
			final ProvisioningCommandRepository provisioningCommandRepository,final ServiceParametersRepository parametersRepository,
			final ProcessRequestRepository processRequestRepository,final OrderRepository orderRepository,
			final ProvisionServiceDetailsRepository provisionServiceDetailsRepository) {
		
		this.context = context;		
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.provisioningCommandRepository=provisioningCommandRepository;
		this.serviceParametersRepository=parametersRepository;
		this.processRequestRepository=processRequestRepository;
		this.orderRepository=orderRepository;
		this.provisionServiceDetailsRepository=provisionServiceDetailsRepository;
		

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
               final Long prepareReqId=command.longValueOfParameterNamed("prepareRequestId");
			//Save in Service_parameter table
			ServiceParameters serviceParameters=ServiceParameters.fromJson(command);
			this.serviceParametersRepository.saveAndFlush(serviceParameters);
			
			ProcessRequest processRequest=new ProcessRequest(serviceParameters.getClientId(),serviceParameters.getOrderId(), 
					"NETSPAN", 'N', null,UserActionStatusTypeEnum.ACTIVATION.toString(), prepareReqId);
			Order order=this.orderRepository.findOne(serviceParameters.getOrderId());
			List<OrderLine> orderLines=order.getServices();
			
			for(OrderLine orderLine:orderLines){
				 ProvisionServiceDetails provisionServiceDetails=this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getServiceId());
				ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),provisionServiceDetails.getServiceIdentification(),"Recieved",
						  serviceParameters.getMacId(),order.getStartDate(),order.getEndDate(),null,null,'N',UserActionStatusTypeEnum.ACTIVATION.toString());
				  processRequest.add(processRequestDetails);
				
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);
			  
			
			
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			
		}
		// TODO Auto-generated method stub
		return null;
	}
	
}

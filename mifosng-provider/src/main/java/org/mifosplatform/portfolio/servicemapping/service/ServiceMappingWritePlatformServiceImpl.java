package org.mifosplatform.portfolio.servicemapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.portfolio.servicemapping.serialization.ServiceMappingCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

	
@Service
public class ServiceMappingWritePlatformServiceImpl implements ServiceMappingWritePlatformService{
	
private final static Logger logger = (Logger) LoggerFactory.getLogger(ServiceMappingWritePlatformServiceImpl.class);	
	
	private PlatformSecurityContext platformSecurityContext;
	private ServiceMappingRepository serviceMappingRepository; 
	private ServiceMappingCommandFromApiJsonDeserializer serviceMappingCommandFromApiJsonDeserializer;
//	private ServiceMappingReadPlatformService serviceMappingReadPlatformService; 
	
	@Autowired
	public ServiceMappingWritePlatformServiceImpl(final PlatformSecurityContext platformSecurityContext, final ServiceMappingRepository serviceMappingRepository, final ServiceMappingCommandFromApiJsonDeserializer serviceMappingCommandFromApiJsonDeserializer) {
		this.platformSecurityContext = platformSecurityContext;
		this.serviceMappingRepository = serviceMappingRepository;
		this.serviceMappingCommandFromApiJsonDeserializer = serviceMappingCommandFromApiJsonDeserializer;
	//	this.serviceMappingReadPlatformService = serviceMappingReadPlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createServiceMapping(JsonCommand command) {
		
		try{
			platformSecurityContext.authenticatedUser();
			serviceMappingCommandFromApiJsonDeserializer.validateForCreate(command.json());
			ServiceMapping serviceMapping = ServiceMapping.fromJson(command);
			serviceMappingRepository.save(serviceMapping);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(serviceMapping.getId()).build();
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Override
	public CommandProcessingResult updateServiceMapping(Long serviceMapId,JsonCommand command) {
			try
			{
				
				   this.platformSecurityContext.authenticatedUser();
		            this.serviceMappingCommandFromApiJsonDeserializer.validateForCreate(command.json());
		            final ServiceMapping  serviceMapping = retrieveServiceMappingById(serviceMapId);
	 		        final Map<String, Object> changes = serviceMapping.update(command);
	                this.serviceMappingRepository.save(serviceMapping);

	             return new CommandProcessingResultBuilder() //
	         .withCommandId(command.commandId()) //
	         .withEntityId(serviceMapId) //
	         .with(changes) //
	         .build();
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {


		 Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("service_code_key")) {
	            final String name = command.stringValueOfParameterNamed("serviceId");
	            throw new PlatformDataIntegrityException("error.msg.service.mapping.duplicate", "A code with name '" + name + "' already exists");
	            //throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
	        }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	
		
	}

	private ServiceMapping retrieveServiceMappingById(Long serviceMapId) {

		
			  final ServiceMapping serviceMapping = this.serviceMappingRepository.findOne(serviceMapId);
		        if (serviceMapping == null) { throw new CodeNotFoundException(serviceMapId.toString()); }
		        return serviceMapping;
		}
	}



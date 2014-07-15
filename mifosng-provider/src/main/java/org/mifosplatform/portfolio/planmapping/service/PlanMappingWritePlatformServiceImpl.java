package org.mifosplatform.portfolio.planmapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.planmapping.domain.PlanMapping;
import org.mifosplatform.portfolio.planmapping.domain.PlanMappingRepository;
import org.mifosplatform.portfolio.planmapping.serialization.PlanMappingCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PlanMappingWritePlatformServiceImpl implements PlanMappingWritePlatformService {
	
	private final PlatformSecurityContext context;
	private final PlanMappingCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final PlanMappingRepository planMappingRepository; 
	
	@Autowired
	public PlanMappingWritePlatformServiceImpl(final PlatformSecurityContext context,
			final PlanMappingCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final PlanMappingRepository planMappingRepository ) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.planMappingRepository = planMappingRepository;
		
	}

	@Override
	public CommandProcessingResult createProvisioningPlanMapping(JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			PlanMapping planMapping = PlanMapping.fromJson(command);
			this.planMappingRepository.save(planMapping);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(planMapping.getId()).build(); 		
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}

	}
	
	@Override
	public CommandProcessingResult updateProvisioningPlanMapping(Long planMapId,JsonCommand command) {
		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());			
			final PlanMapping planMapping = retrievePlanMappingById(planMapId);			
			final Map<String, Object> changes = planMapping.update(command);			
			this.planMappingRepository.save(planMapping);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(planMapId).with(changes).build();		
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
	
	private PlanMapping retrievePlanMappingById(Long planMapId) {
		
		  final PlanMapping planMapping = this.planMappingRepository.findOne(planMapId);
	        if (planMapping == null) { throw new CodeNotFoundException(planMapId.toString()); }
	        return planMapping;
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {


		 Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("plan_id_key")) {
	            final String name = command.stringValueOfParameterNamed("planId");
	            throw new PlatformDataIntegrityException("error.msg.service.mapping.duplicate", "A code with name '" + name + "' already exists");
	            //throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
	        }
	        
	        if (realCause.getMessage().contains("plan_identification_key")) {
	            final String name = command.stringValueOfParameterNamed("planIdentification");
	            throw new PlatformDataIntegrityException("error.msg.service.mapping.duplicate", "A code with name '" + name + "' already exists");
	            //throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
	        }
	        
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	
		
	}

}

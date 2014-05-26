
package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.organisation.hardwareplanmapping.domain.HardwarePlanMapper;
import org.mifosplatform.organisation.hardwareplanmapping.domain.HardwarePlanMapperRepository;
import org.mifosplatform.organisation.hardwareplanmapping.exception.ItemCodeDuplicateException;
import org.mifosplatform.organisation.hardwareplanmapping.handler.HardwarePlanWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.PlanHardWareDetailsRepository;
import org.mifosplatform.portfolio.plan.domain.VolumeDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class HardwarePlanWritePlatformServiceImpl implements HardwarePlanWritePlatformService {
	 private final static Logger logger = LoggerFactory.getLogger(HardwarePlanWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final HardwarePlanMapperRepository   hardwarePlanMapperRepository;
	private final HardwarePlanCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final HardwarePlanReadPlatformService hardwarePlanReadPlatformService;
	
	@Autowired
	public HardwarePlanWritePlatformServiceImpl(final PlatformSecurityContext context,PlanHardWareDetailsRepository planHardWareDetailsRepository,
			final HardwarePlanMapperRepository hardwarePlanMapperRepository,
			final VolumeDetailsRepository volumeDetailsRepository,final HardwarePlanCommandFromApiJsonDeserializer fromApiJsonDeserializer,final HardwarePlanReadPlatformService hardwarePlanReadPlatformService) {
		
		this.context = context;
		this.hardwarePlanMapperRepository = hardwarePlanMapperRepository;
		this.fromApiJsonDeserializer=fromApiJsonDeserializer;
		this.hardwarePlanReadPlatformService=hardwarePlanReadPlatformService;
	
	}

	@Override
	public CommandProcessingResult createHardwarePlan(JsonCommand command) {

		try {

			 this.context.authenticatedUser();
		     this.fromApiJsonDeserializer.validateForCreate(command.json());
		     HardwarePlanMapper harwarePlan=HardwarePlanMapper.fromJson(command);
              List<HardwarePlanData> datas=this.hardwarePlanReadPlatformService.retrieveItems(harwarePlan.getItemCode());
              for(HardwarePlanData data:datas){
            	  
            	   if(data.getplanCode().equalsIgnoreCase(harwarePlan.getPlanCode())){
            		  throw new ItemCodeDuplicateException(harwarePlan.getPlanCode());   
            	   }
            	  
              }
			 this.hardwarePlanMapperRepository.save(harwarePlan);
			return new CommandProcessingResult(Long.valueOf(harwarePlan.getId()));

		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

	private HardwarePlanMapper retrieveHardwarePlanMappingById(Long planId) {
		  final HardwarePlanMapper hardwarePlanMapper = this.hardwarePlanMapperRepository.findOne(planId);
	        if (hardwarePlanMapper == null) { throw new CodeNotFoundException(planId.toString()); }
	        return hardwarePlanMapper;
	}

	@Override
	public CommandProcessingResult updatePlanMapping(Long planMapId, JsonCommand command) {

		try
		{
			
			     context.authenticatedUser();
	            this.fromApiJsonDeserializer.validateForCreate(command.json());
	            final HardwarePlanMapper  hardwarePlanMapper = retrieveHardwarePlanMappingById(planMapId);
 		        final Map<String, Object> changes = hardwarePlanMapper.update(command);
                this.hardwarePlanMapperRepository.save(hardwarePlanMapper);

             return new CommandProcessingResultBuilder() //
         .withCommandId(command.commandId()) //
         .withEntityId(planMapId) //
         .with(changes) //
         .build();
	} catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	
	}

	
	
	


}

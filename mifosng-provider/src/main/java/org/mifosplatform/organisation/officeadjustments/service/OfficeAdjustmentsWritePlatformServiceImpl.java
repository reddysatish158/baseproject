package org.mifosplatform.organisation.officeadjustments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.officeadjustments.domain.OfficeAdjustments;
import org.mifosplatform.organisation.officeadjustments.domain.OfficeAdjustmentsRepository;
import org.mifosplatform.organisation.officeadjustments.serializer.OfficeAdjustmentsCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfficeAdjustmentsWritePlatformServiceImpl implements
		OfficeAdjustmentsWritePlatformService {

	private final PlatformSecurityContext context;
	private final OfficeAdjustmentsRepository officeAdjustmentsRepository;
	private final OfficeAdjustmentsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	
	@Autowired
	public OfficeAdjustmentsWritePlatformServiceImpl(final PlatformSecurityContext context,final OfficeAdjustmentsRepository officeAdjustmentsRepository,
												final OfficeAdjustmentsCommandFromApiJsonDeserializer	fromApiJsonDeserializer){
		this.context = context;
		this.officeAdjustmentsRepository = officeAdjustmentsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createOfficeAdjustment(JsonCommand command) {
		
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final OfficeAdjustments officeAdjustment = OfficeAdjustments.fromJson(command);
			this.officeAdjustmentsRepository.save(officeAdjustment);
			return new CommandProcessingResult(Long.valueOf(officeAdjustment.getId()));
		}catch(DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		}
	}

}

package org.mifosplatform.organisation.officepayments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.officepayments.domain.OfficePayments;
import org.mifosplatform.organisation.officepayments.domain.OfficePaymentsRepository;
import org.mifosplatform.organisation.officepayments.serialization.OfficePaymentsCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfficePaymentsWritePlatformServiceImpl implements OfficePaymentsWritePlatformService {
	
	private final static Logger logger = LoggerFactory.getLogger(OfficePaymentsWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final OfficePaymentsRepository officePaymentsRepository;
	private final OfficePaymentsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	@Autowired
	public OfficePaymentsWritePlatformServiceImpl(final PlatformSecurityContext context,final OfficePaymentsRepository  officePaymentsRepository,
			final OfficePaymentsCommandFromApiJsonDeserializer fromApiJsonDeserializer){
		
		this.context = context;
		this.officePaymentsRepository = officePaymentsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
	}

	@Transactional
	@Override
	public CommandProcessingResult createOfficePayment(JsonCommand command) {

		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final OfficePayments officePayments = OfficePayments.fromJson(command);
			this.officePaymentsRepository.save(officePayments);
			return new CommandProcessingResult(Long.valueOf(officePayments.getId()));
		}catch(DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
	}
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("receipt_no")) {
	            final String name = command.stringValueOfParameterNamed("receiptNo");
	            throw new PlatformDataIntegrityException("error.msg.officePayment_receiptNo.duplicate.name", "A Receipt Number with this Code'"
	                    + name + "'already exists", "receiptNo", name);
	        }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}


}

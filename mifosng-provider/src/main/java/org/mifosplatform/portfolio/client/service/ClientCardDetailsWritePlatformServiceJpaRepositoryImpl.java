package org.mifosplatform.portfolio.client.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.portfolio.client.command.ClientIdentifierCommand;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientCardDetails;
import org.mifosplatform.portfolio.client.domain.ClientCardDetailsRepository;
import org.mifosplatform.portfolio.client.domain.ClientIdentifier;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.serialization.ClientCardDetailsCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.client.serialization.ClientIdentifierCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ClientCardDetailsWritePlatformServiceJpaRepositoryImpl implements ClientCardDetailsWritePlatformService {

	
	 private final static Logger logger = LoggerFactory.getLogger(ClientIdentifierWritePlatformServiceJpaRepositoryImpl.class);

	    private final PlatformSecurityContext context;
	    private final ClientRepositoryWrapper clientRepository;
	    private final ClientCardDetailsRepository clientCardDetailsRepository;
	    private final CodeValueRepositoryWrapper codeValueRepository;
	    private final ClientCardDetailsCommandFromApiJsonDeserializer clientCardDetailsCommandFromApiJsonDeserializer;

	    @Autowired
	    public ClientCardDetailsWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
	            final ClientRepositoryWrapper clientRepository, final ClientCardDetailsRepository clientCardDetailsRepository,
	            final CodeValueRepositoryWrapper codeValueRepository,
	            final ClientCardDetailsCommandFromApiJsonDeserializer clientCardDetailsCommandFromApiJsonDeserializer) {
	    	
	        this.context = context;
	        this.clientRepository = clientRepository;
	        this.clientCardDetailsRepository = clientCardDetailsRepository;
	        this.codeValueRepository = codeValueRepository;
	        this.clientCardDetailsCommandFromApiJsonDeserializer = clientCardDetailsCommandFromApiJsonDeserializer;
	    }
	
	@Override
	public CommandProcessingResult addClientCardDetails(Long clientId,JsonCommand command) {

		this.context.authenticatedUser();
		this.clientCardDetailsCommandFromApiJsonDeserializer.validateForCreate(command.json());

		ClientCardDetails clientCardDetails = ClientCardDetails.fromJson(command);
		final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
		clientCardDetails.setClient(client);

		this.clientCardDetailsRepository.save(clientCardDetails);

		return new CommandProcessingResultBuilder() //
				.withCommandId(command.commandId()) //
				.withOfficeId(client.officeId()) //
				.withClientId(clientId) //
				.withEntityId(clientCardDetails.getId()) //
				.build();

	}

	@Override
	public CommandProcessingResult updateClientCardDetails(JsonCommand command) {
		this.context.authenticatedUser();
		this.clientCardDetailsCommandFromApiJsonDeserializer.validateForCreate(command.json());
		ClientCardDetails clientCardDetails = this.clientCardDetailsRepository.findOne(command.subentityId());
		final Client client = this.clientRepository.findOneWithNotFoundDetection(command.entityId());	
		final Map<String, Object> changes = clientCardDetails.update(command);
		clientCardDetails.setClient(client);		
        if(!changes.isEmpty()){
        	this.clientCardDetailsRepository.save(clientCardDetails);
        }
		return new CommandProcessingResultBuilder() //
				.withCommandId(command.commandId()) //
				.withOfficeId(client.officeId()) //
				.withClientId(command.entityId()) //
				.withEntityId(clientCardDetails.getId()) //
				.build();
	}

	@Override
	public CommandProcessingResult deleteClientCardDetails(JsonCommand command) {
		this.context.authenticatedUser();
		ClientCardDetails clientCardDetails = this.clientCardDetailsRepository.findOne(command.subentityId());
		clientCardDetails.setIsActive('Y');
		this.clientCardDetailsRepository.save(clientCardDetails);
		return new CommandProcessingResultBuilder()
		.withEntityId(clientCardDetails.getId()) //
		.build();
	}

}

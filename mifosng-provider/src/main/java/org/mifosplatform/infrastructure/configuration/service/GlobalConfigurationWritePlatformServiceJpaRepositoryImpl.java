/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.service;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.mifosplatform.infrastructure.codes.domain.Code;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.GlobalConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.configuration.serialization.GlobalConfigurationCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class GlobalConfigurationWritePlatformServiceJpaRepositoryImpl implements GlobalConfigurationWritePlatformService {

    private final PlatformSecurityContext context;
    private final GlobalConfigurationRepository repository;
    private final GlobalConfigurationCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final GlobalConfigurationDataValidator globalConfigurationDataValidator;

    @Autowired
    public GlobalConfigurationWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final GlobalConfigurationRepository codeRepository, final GlobalConfigurationCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final GlobalConfigurationDataValidator globalConfigurationDataValidator) {
        this.context = context;
        this.repository = codeRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.globalConfigurationDataValidator=globalConfigurationDataValidator;
    }

    
    @Transactional
    @Override
    public CommandProcessingResult update(final Long configId, final JsonCommand command) {

        this.context.authenticatedUser();

        try {
            this.globalConfigurationDataValidator.validateForUpdate(command);

            final GlobalConfigurationProperty configItemForUpdate = this.repository.findOne(configId);

            final Map<String, Object> changes = configItemForUpdate.update(command);

            if (!changes.isEmpty()) {
                this.repository.save(configItemForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(configId).with(changes).build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }

    }
    
    @Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
			
			this.context.authenticatedUser();
			//this.globalConfigurationDataValidator.validateForCreate(command);
			final String userName = command.stringValueOfParameterNamed("userName");
			final String mailId = command.stringValueOfParameterNamed("mailId");
			final String password = command.stringValueOfParameterNamed("password");
			final String hostName=command.stringValueOfParameterNamed("hostName");
			final String port=command.stringValueOfParameterNamed("port");
						
			
			final String unencodedPassword = password;
			String encodedString = Base64.encodeBase64String(unencodedPassword.getBytes());
			
			/*  For Decoding above string
			 * 
			 * byte[] decodeString = Base64.decodeBase64(encodedString);
			 * System.out.println("decodeString: "+new String(decodeString));
			 */

			JsonObject json =new JsonObject();
			json.addProperty("mailId", mailId);
			json.addProperty("password", encodedString);
			json.addProperty("hostName", hostName);
			json.addProperty("port", port);
			final GlobalConfigurationProperty globalConfigurationProperty = GlobalConfigurationProperty.fromJson(command, userName, json.toString());
	        
			this.repository.save(globalConfigurationProperty);
	
			return new CommandProcessingResultBuilder().withEntityId(globalConfigurationProperty.getId()).build();
		}
		catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve);
            return CommandProcessingResult.empty();
        }
	}
    
    private GlobalConfigurationProperty retrieveBy(final String propertyName) {
        final GlobalConfigurationProperty property = this.repository.findOneByName(propertyName);
        if (property == null) { throw new GlobalConfigurationPropertyNotFoundException(propertyName); }
        return property;
    }
    
    private void handleDataIntegrityIssues(final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
    //    logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.globalConfiguration.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}
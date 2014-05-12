package org.mifosplatform.organisation.message.service;

import java.io.IOException;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.message.data.BillingMessageData;
import org.mifosplatform.organisation.message.data.BillingMessageDataForProcessing;

public interface BillingMesssageReadPlatformService {
	
BillingMessageData retrieveMessageTemplate(Long id);

List<BillingMessageData> retrieveAllMessageTemplates();

List<BillingMessageData> retrieveAllMessageTemplateParams();


List<BillingMessageData> retrieveMessageParams(Long entityId);

List<BillingMessageData> retrieveData(Long command,String json, BillingMessageData templateData, List<BillingMessageData> messageparam,
		                   BillingMesssageReadPlatformService billingMesssageReadPlatformService);

List<BillingMessageDataForProcessing> retrieveMessageDataForProcessing();

BillingMessageData retrieveTemplate();

Long retrieveClientId(String hardwareId) throws IOException;






}

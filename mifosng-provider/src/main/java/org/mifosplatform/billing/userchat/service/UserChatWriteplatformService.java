package org.mifosplatform.billing.userchat.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface UserChatWriteplatformService {

	CommandProcessingResult createUserChat(JsonCommand command);

}

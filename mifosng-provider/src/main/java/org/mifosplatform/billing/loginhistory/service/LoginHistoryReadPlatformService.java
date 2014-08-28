package org.mifosplatform.billing.loginhistory.service;

import org.mifosplatform.billing.loginhistory.data.LoginHistoryData;
import org.mifosplatform.billing.selfcare.domain.SelfCare;

public interface LoginHistoryReadPlatformService {

	LoginHistoryData retrieveSessionId(String id);

	int retrieveNumberOfUsers(String username);
	
}

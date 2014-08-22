package org.mifosplatform.billing.loginhistory.service;

import org.mifosplatform.billing.loginhistory.data.LoginHistoryData;

public interface LoginHistoryReadPlatformService {

	LoginHistoryData retrieveSessionId(String id);

}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.billing.message.service;

import org.mifosplatform.billing.message.data.BillingMessageDataForProcessing;



public interface MessagePlatformEmailService {

    String sendToUserEmail(BillingMessageDataForProcessing emailDetail);

	String sendToUserMobile(String message, Long id);

	String createEmail(String pdfFileName, String emailId);
	
	

}

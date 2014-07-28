/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when client resources are not found.
 */
public class ClientNotFoundException extends AbstractPlatformDomainRuleException {

    public ClientNotFoundException(final Long clientId) {
        super("error.msg.client.id.invalid", "Client with identifier " + clientId + " does not exist", clientId);
    }
    
    public ClientNotFoundException(final String email) {
        super("error.msg.mail.not.exist", "Client with mail " + email + " does not exist", email);
    }
}
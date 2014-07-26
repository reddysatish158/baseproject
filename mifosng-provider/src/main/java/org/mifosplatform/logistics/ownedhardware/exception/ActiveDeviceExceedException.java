/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.logistics.ownedhardware.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class ActiveDeviceExceedException extends AbstractPlatformDomainRuleException {

    public ActiveDeviceExceedException(final Long clientId) {
        super("error.msg.no.of.devices.are.exceeded.for.this.client", "Number of devices are exceeded for this client",clientId);
        
    }

   
}
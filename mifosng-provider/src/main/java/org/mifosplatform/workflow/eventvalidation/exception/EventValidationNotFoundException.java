/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.workflow.eventvalidation.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class EventValidationNotFoundException extends AbstractPlatformResourceNotFoundException {

    public EventValidationNotFoundException(final String id) {
        super("error.msg.eventvalidation.not.found", "EventValidation with this id"+id+"not exist",id);
        
    }

   
}
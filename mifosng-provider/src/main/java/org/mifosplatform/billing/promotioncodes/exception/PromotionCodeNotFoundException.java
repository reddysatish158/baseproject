package org.mifosplatform.billing.promotioncodes.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class PromotionCodeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public PromotionCodeNotFoundException(final String id) {
        super("error.msg.promotionCode.not.found", "PromotionCode with this id"+id+"not exist",id);
        
    }

   
}
package org.mifosplatform.billing.promotioncodes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionCodeRepository extends

JpaRepository<PromotionCode, Long>,
JpaSpecificationExecutor<PromotionCode>{

}

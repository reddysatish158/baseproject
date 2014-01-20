package org.mifosplatform.billing.promotioncodes.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionCodeDataRepository extends

JpaRepository<PromotionCodeData, Long>,
JpaSpecificationExecutor<PromotionCodeData>{

}

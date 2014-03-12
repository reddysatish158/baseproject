package org.mifosplatform.billing.creditdistribution.domain;


import org.mifosplatform.billing.creditdistribution.domain.CreditDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditDistributionRepository  extends
JpaRepository<CreditDistribution, Long>,
JpaSpecificationExecutor<CreditDistribution>{

}

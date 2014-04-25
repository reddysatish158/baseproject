package org.mifosplatform.portfolio.contract.domain;


import org.mifosplatform.portfolio.contract.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriptionRepository  extends
JpaRepository<Contract, Long>,
JpaSpecificationExecutor<Contract>{


}

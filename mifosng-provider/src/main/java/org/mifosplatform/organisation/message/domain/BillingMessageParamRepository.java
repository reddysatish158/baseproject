package org.mifosplatform.organisation.message.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BillingMessageParamRepository extends JpaRepository<BillingMessageParam, Long>, JpaSpecificationExecutor<BillingMessageParam>
{

}

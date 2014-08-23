package org.mifosplatform.billing.loginhistory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginHistoryRepository extends

JpaRepository<LoginHistory, Long>,
JpaSpecificationExecutor<LoginHistory>{

}

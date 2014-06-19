package org.mifosplatform.finance.billingmaster.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BillDetailRepository extends  JpaRepository<BillDetail, Long>,
JpaSpecificationExecutor<BillDetail>{

}

package org.mifosplatform.billing.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDiscountRepository  extends
JpaRepository<OrderDiscount, Long>,
JpaSpecificationExecutor<OrderDiscount>{

}

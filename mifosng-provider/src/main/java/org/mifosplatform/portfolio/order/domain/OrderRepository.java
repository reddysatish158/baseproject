package org.mifosplatform.portfolio.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository  extends
JpaRepository<Order, Long>,
JpaSpecificationExecutor<Order>{

}

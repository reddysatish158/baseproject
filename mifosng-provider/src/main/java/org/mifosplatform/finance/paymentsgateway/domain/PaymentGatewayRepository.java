package org.mifosplatform.finance.paymentsgateway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long>, JpaSpecificationExecutor<PaymentGateway> 
{

}

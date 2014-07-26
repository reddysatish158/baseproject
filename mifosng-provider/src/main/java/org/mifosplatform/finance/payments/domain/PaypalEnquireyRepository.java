package org.mifosplatform.finance.payments.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaypalEnquireyRepository extends JpaRepository<PaypalEnquirey, Long>,
JpaSpecificationExecutor<PaypalEnquirey> {

}

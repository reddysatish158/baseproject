package org.mifosplatform.finance.billingorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceTaxRepository extends  JpaRepository<InvoiceTax, Long>,
JpaSpecificationExecutor<InvoiceTax>{

}

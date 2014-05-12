package org.mifosplatform.organisation.officepayments.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfficePaymentsRepository extends JpaRepository<OfficePayments, Long>,
		JpaSpecificationExecutor<OfficePayments> {

}

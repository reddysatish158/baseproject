package org.mifosplatform.logistics.itemdetails.mrn.api;

import org.mifosplatform.billing.logistics.itemdetails.domain.MRNDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MRNDetailsJpaRepository extends JpaRepository<MRNDetails, Long>,
		JpaSpecificationExecutor<MRNDetails> {

}

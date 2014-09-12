package org.mifosplatform.organisation.officeadjustments.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfficeAdjustmentsRepository extends JpaRepository<OfficeAdjustments, Long>, JpaSpecificationExecutor<OfficeAdjustments>{

}

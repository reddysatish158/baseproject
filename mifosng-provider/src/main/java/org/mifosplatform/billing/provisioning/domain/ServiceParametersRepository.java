package org.mifosplatform.billing.provisioning.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceParametersRepository  extends
JpaRepository<ServiceParameters, Long>,
JpaSpecificationExecutor<ServiceParameters>{

}

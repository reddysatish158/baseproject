package org.mifosplatform.billing.order.domain;

import org.mifosplatform.billing.provisioning.domain.ServiceParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceParametersRepository  extends
JpaRepository<ServiceParameters, Long>,
JpaSpecificationExecutor<ServiceParameters>{

}

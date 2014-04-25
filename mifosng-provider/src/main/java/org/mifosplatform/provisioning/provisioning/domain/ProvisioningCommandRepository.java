package org.mifosplatform.provisioning.provisioning.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProvisioningCommandRepository extends
JpaRepository<ProvisioningCommand, Long>{

}

package org.mifosplatform.logistics.ownedhardware.domain;

import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OwnedHardwareJpaRepository extends JpaRepository<OwnedHardware, Long>,JpaSpecificationExecutor<OwnedHardware> {
}

package org.mifosplatform.billing.provisioning.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceParametersRepository  extends JpaRepository<ServiceParameters, Long>, JpaSpecificationExecutor<ServiceParameters>{
	
	
	List<ServiceParameters> findDataByOrderId(Long orderId);

}

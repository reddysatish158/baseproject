package org.mifosplatform.portfolio.service.domain;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface ProvisionServiceDetailsRepository extends JpaRepository<ProvisionServiceDetails,Long>, 
JpaSpecificationExecutor<ProvisionServiceDetails>{
	
	List<ProvisionServiceDetails> findOneByServiceId(Long serviceId);

}

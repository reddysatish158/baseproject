package org.mifosplatform.provisioning.provisioning.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceParametersRepository  extends JpaRepository<ServiceParameters, Long>, JpaSpecificationExecutor<ServiceParameters>{
	
	
	@Query("from ServiceParameters serviceParameter where serviceParameter.orderId =:orderId and serviceParameter.status ='ACTIVE'")
	List<ServiceParameters> findDataByOrderId(@Param("orderId") Long orderId);
	
	
	@Query("from ServiceParameters serviceParameter where serviceParameter.clientId =:clientId and serviceParameter.status ='ACTIVE' " +
			" and serviceParameter.parameterName ='GROUP_NAME'")
	List<ServiceParameters> findGroupNameByclientId(@Param("clientId") Long clientId);

}

package org.mifosplatform.organisation.redemption.domain;

import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RedemptionRepository extends JpaRepository<RandomGeneratorDetails, Long>,
		JpaSpecificationExecutor<RandomGeneratorDetails> {
	
	@Query("from RandomGeneratorDetails randomDetails where randomDetails.pinNo =:pinNumber and randomDetails.clientId is null")
	RandomGeneratorDetails findOneByPinNumber(@Param("pinNumber") String pinNumber);
	

}

package org.mifosplatform.crm.ticketmaster.domain;

import org.mifosplatform.crm.ticketmaster.domain.TicketMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketMasterRepository  extends
	JpaRepository<TicketMaster, Long>,
	JpaSpecificationExecutor<TicketMaster>{

	}



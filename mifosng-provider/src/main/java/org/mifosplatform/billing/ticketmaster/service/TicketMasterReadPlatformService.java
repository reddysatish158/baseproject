package org.mifosplatform.billing.ticketmaster.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.ticketmaster.data.ClientTicketData;
import org.mifosplatform.billing.ticketmaster.data.ProblemsData;
import org.mifosplatform.billing.ticketmaster.data.TicketMasterData;
import org.mifosplatform.billing.ticketmaster.data.UsersData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface TicketMasterReadPlatformService {

	List<ProblemsData> retrieveProblemData();
	
	/*List<ProblemsData> retrieveStatusData();*/


	List<UsersData> retrieveUsers();

	List<TicketMasterData> retrieveClientTicketDetails(Long clientId);

	TicketMasterData retrieveSingleTicketDetails(Long clientId, Long ticketId);

	List<TicketMasterData> retrieveTicketStatusData();


	List<EnumOptionData> retrievePriorityData();


	List<TicketMasterData> retrieveTicketCloseStatusData();


	List<TicketMasterData> retrieveClientTicketHistory(Long ticketId);


	List<ClientTicketData> retrieveAssignedTickets();

	TicketMasterData retrieveTicket(Long clientId, Long ticketId);
	Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster);
	
	
}

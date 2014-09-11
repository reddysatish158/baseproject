package org.mifosplatform.crm.ticketmaster.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.data.ProblemsData;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.data.UsersData;
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
	Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster, String statusType);
	
	
}

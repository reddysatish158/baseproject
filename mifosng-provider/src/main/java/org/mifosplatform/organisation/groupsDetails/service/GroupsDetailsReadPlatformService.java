package org.mifosplatform.organisation.groupsDetails.service;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.groupsDetails.data.GroupsDetailsData;

public interface GroupsDetailsReadPlatformService {

	public Page<GroupsDetailsData> retrieveAllGroupsData(SearchSqlQuery searchGroupsDetails);

}

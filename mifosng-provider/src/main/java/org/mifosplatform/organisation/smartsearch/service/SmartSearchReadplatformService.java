package org.mifosplatform.organisation.smartsearch.service;

import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.smartsearch.data.SmartSearchData;

public interface SmartSearchReadplatformService {

	Page<SmartSearchData> retrieveAllSearchData(String searchText,Date fromDate, Date toDate,
			Integer limit, Integer offset);

}

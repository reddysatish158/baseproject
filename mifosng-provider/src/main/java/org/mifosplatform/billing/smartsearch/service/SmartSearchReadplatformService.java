package org.mifosplatform.billing.smartsearch.service;

import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.smartsearch.data.SmartSearchData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface SmartSearchReadplatformService {

	Page<SmartSearchData> retrieveAllSearchData(String searchText,Date fromDate, Date toDate,
			Integer limit, Integer offset);

}

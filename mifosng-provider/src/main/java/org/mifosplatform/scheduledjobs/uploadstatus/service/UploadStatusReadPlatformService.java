package org.mifosplatform.scheduledjobs.uploadstatus.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.scheduledjobs.uploadstatus.data.UploadStatusData;

public interface UploadStatusReadPlatformService {

    Collection<UploadStatusData> retrieveAllCodes();
    Page<UploadStatusData> retrieveAllUploadStatusData(SearchSqlQuery searchUploads);

    UploadStatusData retrieveCode(Long codeId);
	UploadStatusData retrieveSingleFileDetails(Long fileId);
}

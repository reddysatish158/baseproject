package org.mifosplatform.scheduledjobs.uploadstatus.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.scheduledjobs.uploadstatus.data.UploadStatusData;

public interface UploadStatusReadPlatformService {

    Collection<UploadStatusData> retrieveAllCodes();
    List<UploadStatusData> retrieveAllUploadStatusData();

    UploadStatusData retrieveCode(Long codeId);
	UploadStatusData retrieveSingleFileDetails(Long fileId);
}

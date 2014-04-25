package org.mifosplatform.cms.mediadetails.domain;

import org.mifosplatform.cms.media.domain.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MediaAssetRepository  extends
JpaRepository<MediaAsset, Long>,
JpaSpecificationExecutor<MediaAsset>{

}

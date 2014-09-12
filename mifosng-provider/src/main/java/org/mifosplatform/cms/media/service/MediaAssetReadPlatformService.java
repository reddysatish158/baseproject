package org.mifosplatform.cms.media.service;

import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.cms.media.data.MediaAssetData;
import org.mifosplatform.cms.media.data.MediaassetAttribute;
import org.mifosplatform.cms.media.data.MediaassetAttributeData;
import org.mifosplatform.cms.mediadetails.data.MediaLocationData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;

public interface MediaAssetReadPlatformService {

	List<MediaAssetData> retrievemediaAssetdata(Long pageNo);
	List<MediaAssetData> retrievemediaAssetdatabyNewRealease(Long pageNo);
	List<MediaAssetData> retrievemediaAssetdatabyRating(Long pageNo);
	List<MediaAssetData> retrieveAllmediaAssetdata();
	Long retrieveNoofPages(String query);
	List<MediaAssetData> retrievemediaAssetdatabyDiscountedMovies(Long pageNo);
	List<MediaAssetData> retrievemediaAssetdatabyPromotionalMovies(Long pageNo);
	List<MediaAssetData> retrievemediaAssetdatabyComingSoonMovies(Long pageNo);
	List<MediaAssetData> retrievemediaAssetdatabyMostWatchedMovies(Long pageNo);
	MediaAssetData retrievemediaAsset(Long mediaId);
	List<MediaAssetData> retrievemediaAssetdatabySearching(Long pageNo,String filterType);
	List<MediaAssetData> retrieveAllAssetdata();
	List<MediaAssetData> retrieveAllMediaTemplatedata();
	List<MediaassetAttribute> retrieveMediaAttributes();
	List<MediaassetAttribute> retrieveMediaFormatType();
	List<MediaEnumoptionData> retrieveMediaTypeData();
	List<McodeData> retrieveMedaiCategory();
	List<McodeData> retrieveLanguageCategeories();
	List<MediaassetAttributeData> retrieveMediaassetAttributesData(Long mediaId);
	List<MediaLocationData> retrievemediaAssetLocationdata(Long mediaId);
	List<McodeData> retrieveContentProviders();


}

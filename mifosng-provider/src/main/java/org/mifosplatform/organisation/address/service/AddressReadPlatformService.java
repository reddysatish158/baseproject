package org.mifosplatform.organisation.address.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.AddressDetails;
import org.mifosplatform.organisation.address.data.CountryDetails;

public interface AddressReadPlatformService {


	List<AddressData> retrieveSelectedAddressDetails(String selectedname);

	List<AddressData> retrieveAddressDetails(Long clientId);

	List<AddressData> retrieveAddressDetails();
	
	List<String> retrieveCountryDetails();

	List<String> retrieveStateDetails();

	List<String> retrieveCityDetails();

	List<AddressData> retrieveCityDetails(String selectedname);

	List<EnumOptionData> addressType();

	AddressData retrieveName(String name);

	List<CountryDetails> retrieveCountries();
	
	List<AddressData> retrieveClientAddressDetails(Long clientId);
	
	Page<AddressDetails> retrieveAllAddresses(SearchSqlQuery searchAddresses);
	

}


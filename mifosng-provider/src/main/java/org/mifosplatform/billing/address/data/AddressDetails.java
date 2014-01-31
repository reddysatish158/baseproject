package org.mifosplatform.billing.address.data;



public class AddressDetails {
	
  private final String countryName;
  private final String cityName;
  private final String stateName;
  private final Long cityId;
  private final Long countryId;
  private final Long stateId;
	
	public AddressDetails(String countryName,String cityName, String stateName,Long countryId,Long stateId,Long cityId){
		this.countryName = countryName;
		this.cityName = cityName;
		this.stateName = stateName;
		this.cityId=cityId;
		this.stateId=stateId;
		this.countryId=countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	
	public String getCityName() {
		return cityName;
	}

	

	public String getStateName() {
		return stateName;
	}

	

	public Long getCityId() {
		return cityId;
	}

	

	public Long getCountryId() {
		return countryId;
	}

	

	public Long getStateId() {
		return stateId;
	}

	
}

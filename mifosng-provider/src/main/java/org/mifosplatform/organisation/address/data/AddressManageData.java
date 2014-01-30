package org.mifosplatform.organisation.address.data;



public class AddressManageData {
	
	String countryName;
	String cityName;
	String stateName;
	Long cityId;
	Long countryId;
	Long stateId;
	
	public AddressManageData(String countryName,String cityName, String stateName,Long countryId,Long stateId,Long cityId){
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

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
	

	

	

}

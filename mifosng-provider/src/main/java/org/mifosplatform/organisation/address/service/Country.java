package org.mifosplatform.organisation.address.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;



@Entity
@Table(name="b_country",uniqueConstraints=@UniqueConstraint( name ="country_code",columnNames={"country_code"}))
public class Country extends AbstractPersistable<Long>{
	

@Column(name="country_code")
private String countryCode;

@Column(name="country_name")
private String countryName;

@Column(name="is_active")
private String isActive;


public String getCountryCode() {
	return countryCode;
}


public void setCountryCode(String countryCode) {
	this.countryCode = countryCode;
}


public String getCountryName() {
	return countryName;
}


public void setCountryName(String countryName) {
	this.countryName = countryName;
}


public String getIsActive() {
	return isActive;
}


public void setIsActive(String isActive) {
	this.isActive = isActive;
}

public Country(){
	// TODO Auto-generated constructor stub
}

public Country(String entityCode, String entityName) {
this.countryCode=entityCode;
this.countryName=entityName;
this.isActive="Y";


}


public static Country fromJson(JsonCommand command) {
	final String cityCode = command.stringValueOfParameterNamed("entityCode");
    final String cityName = command.stringValueOfParameterNamed("entityName");
     return new Country(cityCode,cityName);
}
public Map<String, Object> update(JsonCommand command) {
	final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	final String countryCodeParamName="entityCode";
	if (command.isChangeInStringParameterNamed(countryCodeParamName,this.countryCode)){
		final String newValue = command.stringValueOfParameterNamed(countryCodeParamName);
		actualChanges.put(countryCodeParamName, newValue);
		this.countryCode = StringUtils.defaultIfEmpty(newValue, null);
	}
	final String countryNameParamName="entityName";
	if (command.isChangeInStringParameterNamed(countryNameParamName,this.countryName)){
		final String newValue = command.stringValueOfParameterNamed(countryNameParamName);
		actualChanges.put(countryNameParamName, newValue);
		this.countryName = StringUtils.defaultIfEmpty(newValue, null);
	}
	
    return actualChanges;
}


public void delete() {
	// TODO Auto-generated method stub
	if(this.isActive.equalsIgnoreCase(isActive)){
		this.isActive="N";
	}
	
}

}

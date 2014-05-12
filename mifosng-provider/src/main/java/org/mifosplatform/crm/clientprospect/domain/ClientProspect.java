package org.mifosplatform.crm.clientprospect.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.domain.ClientEnumerations;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.useradministration.domain.AppUser;

import com.google.gson.JsonElement;


@Entity
@Table(name="b_prospect")
public class ClientProspect extends AbstractAuditableCustom<AppUser, Long> {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="prospect_type", length=1 )
	private Short prospectType;
	
	@Column(name="first_name", length=50 )
	private String firstName;
	
	@Column(name="middle_name", length=50 )
	private String middleName;
	
	@Column(name="last_name", length=50)
	private String lastName;
	
	@Column(name="home_phone_number", length=20 )
	private String homePhoneNumber;
	
	@Column(name="work_phone_number", length=20 )
	private String workPhoneNumber;
	
	@Column(name="mobile_number", length=20 )
	private String mobileNumber;
	
	@Column(name="email", length=50 )
	private String email;
	
	
	@Column(name="source_of_publicity", length=50 )
	private String sourceOfPublicity;
	
	@Column(name="preferred_plan")
	private String preferredPlan;
	
	@Column(name="preferred_calling_time")
	private Date preferredCallingTime;
	
	@Column(name="note", length=100 )
	private String note;
	
	@Column(name="address", length=100 )
	private String address;
	
	@Column(name="street_area", length=100 )
	private String streetArea;
	
	@Column(name="city_district", length=100 )
	private String cityDistrict;
	
	@Column(name="state", length=100)
	private String state;
	
	@Column(name="country", length=100)
	private String country;

	@Column(name="status", length=100)
	private String status="New";
	
	@Column(name="status_remark", length=50)
	private String statusRemark;
	
	@Column(name="zip_code")
	private String zipCode;
	
	@Column(name="is_deleted")
	private char isDeleted='N';
	
	public ClientProspect() {
		
	}
	
	public ClientProspect(final Short prospectType, final String firstName, final String middleName, final String lastName, final String homePhoneNumber, final String workPhoneNumber, final String mobileNumber, final String email, final String sourceOfPublicity, final Date preferredCallingTime, final String note, final String address, final String streetArea, final String cityDistrict, final String state, final String country,final String zipCode){
		this.prospectType = prospectType;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicity = sourceOfPublicity;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
	}
	
	public ClientProspect(final Short prospectType, final String firstName, final String middleName, final String lastName, final String homePhoneNumber, final String workPhoneNumber, final String mobileNumber, final String email, final String sourceOfPublicity, final Date preferredCallingTime, final String note, final String address, final String streetArea, final String cityDistrict, final String state, final String country, final String preferredPlan, final String status, final String statusRemark, final String zipCode){
		this.prospectType = prospectType;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicity = sourceOfPublicity;
		this.preferredPlan = preferredPlan;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.status = status;
		this.statusRemark = statusRemark;
		this.zipCode = zipCode;
	}

	public Short getProspectType() {
		return prospectType;
	}

	public void setProspectType(Short prospectType) {
		this.prospectType = prospectType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}

	public void setHomePhoneNumber(String homePhoneNumber) {
		this.homePhoneNumber = homePhoneNumber;
	}

	public String getWorkPhoneNumber() {
		return workPhoneNumber;
	}

	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSourceOfPublicity() {
		return sourceOfPublicity;
	}

	public void setSourceOfPublicity(String sourceOfPublicity) {
		this.sourceOfPublicity = sourceOfPublicity;
	}

	public Date getPreferredCallingTime() {
		return preferredCallingTime;
	}

	public void setPreferredCallingTime(Date preferredCallingTime) {
		this.preferredCallingTime = preferredCallingTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreetArea() {
		return streetArea;
	}

	public void setStreetArea(String streetArea) {
		this.streetArea = streetArea;
	}

	public String getCityDistrict() {
		return cityDistrict;
	}

	public void setCityDistrict(String cityDistrict) {
		this.cityDistrict = cityDistrict;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getPreferredPlan() {
		return preferredPlan;
	}

	public void setPreferredPlan(String preferredPlan) {
		this.preferredPlan = preferredPlan;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	public static ClientProspect fromJson(final FromJsonHelper fromJsonHelper, final JsonCommand command) throws ParseException{
			
		final JsonElement element = fromJsonHelper.parse(command.json());
		
		Short prospectType = command.integerValueOfParameterNamed("prospectType").shortValue();		
		
		ClientProspect clientProspect = new ClientProspect();
		clientProspect.setProspectType(prospectType);
		
		if(fromJsonHelper.parameterExists("firstName", element)){
			String firstName = command.stringValueOfParameterNamed("firstName");
			clientProspect.setFirstName(firstName);
		}
		
		if(fromJsonHelper.parameterExists("middleName", element)){
			String middleName = command.stringValueOfParameterNamed("middleName");
			clientProspect.setMiddleName(middleName);
		}
		
		if(fromJsonHelper.parameterExists("lastName", element)){
			String lastName = command.stringValueOfParameterNamed("lastName");
			clientProspect.setLastName(lastName);
		}
		
		if(fromJsonHelper.parameterExists("homePhoneNumber", element)){
			String homePhoneNumber = command.stringValueOfParameterNamed("homePhoneNumber");
			clientProspect.setHomePhoneNumber(homePhoneNumber);
		}
		
		if(fromJsonHelper.parameterExists("workPhoneNumber", element)){
			String workPhoneNumber = command.stringValueOfParameterNamed("workPhoneNumber");
			clientProspect.setWorkPhoneNumber(workPhoneNumber);
		}
		
		if(fromJsonHelper.parameterExists("mobileNumber", element)){
			String mobileNumber = command.stringValueOfParameterNamed("mobileNumber");
			clientProspect.setMobileNumber(mobileNumber);
		}
		
		if(fromJsonHelper.parameterExists("email", element)){
			String email = command.stringValueOfParameterNamed("email");
			clientProspect.setEmail(email);
		}
		
		if(fromJsonHelper.parameterExists("sourceOfPublicity", element)|| fromJsonHelper.parameterExists("sourceOther", element)){
			String sourceOfPublicity = command.stringValueOfParameterNamed("sourceOfPublicity");
			
			if(sourceOfPublicity.equalsIgnoreCase("Other")){
				String sourceOther = command.stringValueOfParameterNamed("sourceOther");
				clientProspect.setSourceOfPublicity(sourceOther);
			}else{
				clientProspect.setSourceOfPublicity(sourceOfPublicity);
			}
			
		}
		
		
		
		if(fromJsonHelper.parameterExists("preferredCallingTime", element)){
			String startDateString = command.stringValueOfParameterNamed("preferredCallingTime");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date preferredCallingTime = df.parse(startDateString);
			clientProspect.setPreferredCallingTime(preferredCallingTime);
		}
		
		if(fromJsonHelper.parameterExists("note", element)){
			String note = command.stringValueOfParameterNamed("note");
			clientProspect.setNote(note);
		}
		
		if(fromJsonHelper.parameterExists("address", element)){
			String address = command.stringValueOfParameterNamed("address");
			clientProspect.setAddress(address);
		}
		
		if(fromJsonHelper.parameterExists("streetArea", element)){
			String streetArea = command.stringValueOfParameterNamed("streetArea");
			clientProspect.setStreetArea(streetArea);
		}
		
		if(fromJsonHelper.parameterExists("cityDistrict", element)){
			String cityDistrict = command.stringValueOfParameterNamed("cityDistrict");
			clientProspect.setCityDistrict(cityDistrict);
		}
		
		if(fromJsonHelper.parameterExists("state", element)){
			String state = command.stringValueOfParameterNamed("state");
			clientProspect.setState(state);
		}
		
		if(fromJsonHelper.parameterExists("country", element)){
			String country = command.stringValueOfParameterNamed("country");
			clientProspect.setCountry(country);
		}
		
		if(fromJsonHelper.parameterExists("preferredPlan", element)){
			String preferredPlan = command.stringValueOfParameterNamed("preferredPlan");
			clientProspect.setPreferredPlan(preferredPlan);
		}
		
		if(fromJsonHelper.parameterExists("status", element)){
			String status = command.stringValueOfParameterNamed("status");
			clientProspect.setStatus(status);
		}
		
		if(fromJsonHelper.parameterExists("zipCode", element))
		{
			String zipCode = command.stringValueOfParameterNamed("zipCode");
			clientProspect.setZipCode(zipCode);
		}
		
		return clientProspect;
	}

	public Map<String,Object> update(final JsonCommand command){
		Map<String,Object> actualChanges = new LinkedHashMap<String, Object>(1);

		//prospectType","firstName","middleName","lastName","homePhoneNumber","workPhoneNumber","mobileNumber","email",
		//"sourceOfPublicity","preferredCallingTime","note","address","streetArea","cityDistrict","state
		//","country","locale","preferredPlan","status","statusRemark","callStatus","assignedTo","notes","isDeleted","zipCode"
		
	        final String prospectType = "prospectType";final String firstName = "firstName";
	        final String middleName = "middleName";final String lastName = "lastName";
	        final String homePhoneNumber = "homePhoneNumber"; final String workPhoneNumber = "workPhoneNumber";
	        final String mobileNumber = "mobileNumber"; final String email = "email";
	        final String address = "address"; 
	        final String streetArea = "streetArea";
	        final String cityDistrict = "cityDistrict"; 
	        final String state = "state"; 
	        final String country = "country";
	        final String zipCode = "zipCode";
	        final String sourceOfPublicity = "sourceOfPublicity"; 
	        final String preferredPlan = "preferredPlan";
	        final String preferredCallingTime = "preferredCallingTime"; 
	        final String note = "note";
	        final String sourceOther = "sourceOther";
	        
	        
	        if (command.isChangeInIntegerParameterNamed(prospectType, new Integer(this.prospectType))) {
	        	final Short newValue = command.integerValueOfParameterNamed("prospectType").shortValue();
	            actualChanges.put(prospectType, newValue);
	            this.prospectType = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(firstName, this.firstName)) {
	        	final String newValue = command.stringValueOfParameterNamed("firstName");
	            actualChanges.put(firstName, newValue);
	            this.firstName = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(middleName, this.middleName)) {
	        	final String newValue = command.stringValueOfParameterNamed("middleName");
	            actualChanges.put(middleName, newValue);
	            this.middleName = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(lastName, this.lastName)) {
	        	final String newValue = command.stringValueOfParameterNamed("lastName");
	            actualChanges.put(lastName, newValue);
	            this.lastName = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(homePhoneNumber, this.homePhoneNumber)) {
	        	final String newValue = command.stringValueOfParameterNamed("homePhoneNumber");
	            actualChanges.put(homePhoneNumber, newValue);
	            this.homePhoneNumber = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(workPhoneNumber, this.workPhoneNumber)) {
	        	final String newValue = command.stringValueOfParameterNamed("workPhoneNumber");
	            actualChanges.put(workPhoneNumber, newValue);
	            this.workPhoneNumber = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(mobileNumber, this.mobileNumber)) {
	        	final String newValue = command.stringValueOfParameterNamed("mobileNumber");
	            actualChanges.put(mobileNumber, newValue);
	            this.mobileNumber = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(email, this.email)) {
	        	final String newValue = command.stringValueOfParameterNamed("email");
	            actualChanges.put(email, newValue);
	            this.email = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(address, this.address)) {
	        	final String newValue = command.stringValueOfParameterNamed("address");
	            actualChanges.put(address, newValue);
	            this.address = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(streetArea, this.streetArea)) {
	        	final String newValue = command.stringValueOfParameterNamed("streetArea");
	            actualChanges.put(streetArea, newValue);
	            this.streetArea = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(cityDistrict, this.cityDistrict)) {
	        	final String newValue = command.stringValueOfParameterNamed("cityDistrict");
	            actualChanges.put(cityDistrict, newValue);
	            this.cityDistrict = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(state, this.state)) {
	        	final String newValue = command.stringValueOfParameterNamed("state");
	            actualChanges.put(state, newValue);
	            this.state = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(country, this.country)) {
	        	final String newValue = command.stringValueOfParameterNamed("country");
	            actualChanges.put(country, newValue);
	            this.country = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(zipCode, this.zipCode)) {
	        	final String newValue = command.stringValueOfParameterNamed("zipCode");
	            actualChanges.put(zipCode, newValue);
	            this.zipCode = newValue;
	        }
	       /* if (command.isChangeInStringParameterNamed(sourceOfPublicity, this.sourceOfPublicity)) {
	        	final String newValue = command.stringValueOfParameterNamed("sourceOfPublicity");
	            actualChanges.put(sourceOfPublicity, newValue);
	            this.sourceOfPublicity = newValue;
	        }*/
	        
	        if (command.isChangeInStringParameterNamed(sourceOfPublicity, this.sourceOfPublicity)||command.isChangeInStringParameterNamed(sourceOther, this.sourceOfPublicity)) {
	        	final String newValue = command.stringValueOfParameterNamed("sourceOfPublicity");
	            if(newValue.equalsIgnoreCase("Other")){
	            	final String otherSource = command.stringValueOfParameterNamed("sourceOther");
	            	actualChanges.put(sourceOfPublicity, otherSource);
		            this.sourceOfPublicity = otherSource;
	            }else{
	            	actualChanges.put(sourceOfPublicity, newValue);
		            this.sourceOfPublicity = newValue;
	            }
	        }
	        
	        
	        if (command.isChangeInStringParameterNamed(preferredPlan, this.preferredPlan)) {
	        	final String newValue = command.stringValueOfParameterNamed("preferredPlan");
	            actualChanges.put(preferredPlan, newValue);
	            this.preferredPlan = newValue; 
	        }
	        if (command.isChangeInStringParameterNamed(preferredCallingTime, this.preferredCallingTime.toString())) {
	        	
	        	final String startDateString = command.stringValueOfParameterNamed("preferredCallingTime");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date newValue;
				try {
					newValue = df.parse(startDateString);
				}catch(ParseException pe){
					throw new PlatformDataIntegrityException("invalid.date.and.time.format", "invalid.date.and.time.format", "preferredCallingTime");
				}
	            actualChanges.put(preferredCallingTime, newValue);
	            this.preferredCallingTime = newValue;
	        }
	        if (command.isChangeInStringParameterNamed(note, this.note)) {
	        	final String newValue = command.stringValueOfParameterNamed("note");
	            actualChanges.put(note, newValue);
	            this.note = newValue;
	        }
	        
	        

		
		return actualChanges;
	}

	public String getStatusRemark() {
		return statusRemark;
	}

	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
}

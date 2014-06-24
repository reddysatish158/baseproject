/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;

/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

    private final Long id;
    private final String accountNo;
    private final String externalId;

    private final EnumOptionData status;
    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final String fullname;
    private final String displayName;

    private final Long officeId;
    private final String officeName;

    private final String imageKey;
    @SuppressWarnings("unused")
    private final Boolean imagePresent;
    private final String email;
    private final String phone;
    private final String homePhoneNumber;
    private final String addressNo;
    private final String street;
    private final String city;
    private final String state;
    private final String country;
    private final String zip;
    private final BigDecimal balanceAmount;
    private final String hwSerialNumber;
  
    // associations
    private final Collection<GroupGeneralData> groups;

    // template
    private final Collection<OfficeData> officeOptions;
    
    private final Collection<ClientCategoryData> clientCategoryDatas;
	private final String categoryType;
	private AddressData addressTemplateData;
    private final List<String> hardwareDetails;
    private GlobalConfigurationProperty configurationProperty;
	private  final String currency;
	private final Collection<GroupData> groupNameDatas;
   private final Collection<CodeValueData> closureReasons;
   private final Boolean balanceCheck;

    public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions,
    		Collection<ClientCategoryData> categoryDatas,Collection<GroupData> groupDatas,List<CodeValueData> closureReasons) {
        return new ClientData(null, null, officeId, null, null, null, null, null, null, null, null, joinedDate, null, officeOptions, null,
        		categoryDatas,null,null,null, null, null, null, null, null, null, null,null,null,null,null,groupDatas,closureReasons,null);
        }
	
    public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions, Collection<ClientCategoryData> categoryDatas,
    		List<CodeValueData> closureReasons) {
        return new ClientData(null, null, officeId, null, null, null, null, null, null, null, null, joinedDate, null, officeOptions, null,
        		categoryDatas,null,null,null, null, null, null, null, null, null, null,null,null,null,null,null,closureReasons,null);
    }

    public static ClientData templateOnTop(final ClientData clientData, final List<OfficeData> allowedOffices, Collection<ClientCategoryData> categoryDatas,
    		Collection<GroupData> groupDatas, List<String> allocationDetailsDatas, String balanceCheck) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.officeId, clientData.officeName, clientData.id,
                clientData.firstname, clientData.middlename, clientData.lastname, clientData.fullname, clientData.displayName,
                clientData.externalId, clientData.activationDate, clientData.imageKey, allowedOffices, clientData.groups,
                categoryDatas,clientData.categoryType,clientData.email,clientData.phone,clientData.homePhoneNumber,clientData.addressNo,clientData.street,
                clientData.city,clientData.state,clientData.country,clientData.zip,clientData.balanceAmount,allocationDetailsDatas,clientData.hwSerialNumber,
                clientData.currency, groupDatas,null,balanceCheck);
    }

    public static ClientData setParentGroups(final ClientData clientData, final Collection<GroupGeneralData> parentGroups) {
        return new ClientData(clientData.accountNo, clientData.status, clientData.officeId, clientData.officeName, clientData.id,
                clientData.firstname, clientData.middlename, clientData.lastname, clientData.fullname, clientData.displayName,
                clientData.externalId, clientData.activationDate, clientData.imageKey, clientData.officeOptions, parentGroups,
                clientData.clientCategoryDatas,clientData.categoryType,clientData.email,clientData.phone,clientData.homePhoneNumber,
                clientData.addressNo,clientData.street,clientData.city,clientData.state,clientData.country,clientData.zip,clientData.balanceAmount,
                clientData.hardwareDetails,clientData.hwSerialNumber,clientData.currency, clientData.groupNameDatas,null,null);
    }

    public static ClientData clientIdentifier(final Long id, final String accountNo, final EnumOptionData status, final String firstname,
            final String middlename, final String lastname, final String fullname, final String displayName, final Long officeId,
            final String officeName) {

        return new ClientData(accountNo, status, officeId, officeName, id, firstname, middlename, lastname, fullname, displayName, null,
                null, null, null, null,null,null,null,null, null,null, null,null, null, null,null,null,null,null,null, null,null,null);
    }

    public static ClientData lookup(final Long id, final String displayName, final Long officeId, final String officeName) {
        return new ClientData(null, null, officeId, officeName, id, null, null, null, null, displayName, null, null, null, null, null,null,null,null,null,
        		null,null,null, null,null,null,null,null,null,null,null, null,null,null);
    }

    public static ClientData instance(final String accountNo, final EnumOptionData status, final Long officeId, final String officeName,final Long id, 
    		final String firstname, final String middlename, final String lastname, final String fullname,final String displayName, final String externalId,
    		final LocalDate activationDate, final String imageKey,final String categoryType,final String email,final String phone,final String homePhoneNumber,final String addrNo,final String street,
    		final String city,final String state,final String country,final String zip,final BigDecimal balanceAmount,final String hwSerialNumber,final String currency) {
    	
        return new ClientData(accountNo, status, officeId, officeName, id, firstname, middlename, lastname, fullname, displayName,
                externalId, activationDate, imageKey, null, null,null,categoryType,email,phone,homePhoneNumber,addrNo,street,city,state,country,zip,
                balanceAmount,null,hwSerialNumber,currency, null,null,null);
    }

    private ClientData(final String accountNo, final EnumOptionData status, final Long officeId, final String officeName, final Long id,final String firstname,
    		final String middlename, final String lastname, final String fullname, final String displayName,final String externalId, final LocalDate activationDate, 
    		final String imageKey, final Collection<OfficeData> allowedOffices,final Collection<GroupGeneralData> groups, Collection<ClientCategoryData> clientCategoryDatas,
    		final String categoryType,final String email,final String phone,final String homePhoneNumber,final String addrNo,final String street,final String city,final String state,
    		final String country,final String zip, BigDecimal balanceAmount,final List<String> hardwareDetails,final String hwSerialNumber,final String currency, Collection<GroupData> groupNameDatas, 
    		List<CodeValueData> closureReasons, String balanceCheck) {
        this.accountNo = accountNo;
        this.status = status;
        if (status != null) {
            active = status.getId().equals(300L);
        } else {
            active = null;
        }
        this.officeId = officeId;
        this.officeName = officeName;
        this.id = id;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.middlename = StringUtils.defaultIfEmpty(middlename, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.fullname = StringUtils.defaultIfEmpty(fullname, null);
        this.displayName = StringUtils.defaultIfEmpty(displayName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.activationDate = activationDate;
        this.imageKey = imageKey;
        if (imageKey != null) {
            this.imagePresent = Boolean.TRUE;
        } else {
            this.imagePresent = null;
        }
        this.closureReasons=closureReasons;

        // associations
        this.groups = groups;

        // template
        this.officeOptions = allowedOffices;
        this.clientCategoryDatas=clientCategoryDatas;
        this.groupNameDatas = groupNameDatas;
        this.categoryType=categoryType;
        this.email=email;
        this.phone=phone;
        this.homePhoneNumber=homePhoneNumber;
        this.addressNo= StringUtils.defaultIfEmpty(addrNo, null);
        this.street= StringUtils.defaultIfEmpty(street, null);
        this.city= StringUtils.defaultIfEmpty(city, null);
        this.state= StringUtils.defaultIfEmpty(state, null);
        this.country= StringUtils.defaultIfEmpty(country, null);
        this.zip= StringUtils.defaultIfEmpty(zip, null);
        if(balanceAmount==null){
        	balanceAmount=BigDecimal.ZERO;
		}
        this.balanceAmount=balanceAmount!=null?balanceAmount:BigDecimal.ZERO;
        this.hardwareDetails=hardwareDetails;
        this.hwSerialNumber=hwSerialNumber;
        this.currency=currency;
       if(balanceCheck !=null && balanceCheck.equalsIgnoreCase("Y"))
    	   this.balanceCheck=true;
       else
    	   this.balanceCheck=false;
        
    }

	public Long id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String officeName() {
        return this.officeName;
    }

    public String imageKey() {
        return this.imageKey;
    }

    public boolean imageKeyDoesNotExist() {
        return !imageKeyExists();
    }

    private boolean imageKeyExists() {
        return StringUtils.isNotBlank(this.imageKey);
    }

    @Override
    public int compareTo(final ClientData obj) {
        if (obj == null) { return -1; }
        return new CompareToBuilder() //
                .append(this.id, obj.id) //
                .append(this.displayName, obj.displayName) //
                .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        ClientData rhs = (ClientData) obj;
        return new EqualsBuilder() //
                .append(this.id, rhs.id) //
                .append(this.displayName, rhs.displayName) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37) //
                .append(this.id) //
                .append(this.displayName) //
                .toHashCode();
    }

    // TODO - kw - look into removing usage of the getters below
    public String getExternalId() {
        return this.externalId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }

	public void setAddressTemplate(AddressData data) {
		this.addressTemplateData=data;
		
	}

	public GlobalConfigurationProperty getConfigurationProperty() {
		return configurationProperty;
	}

	public void setConfigurationProperty(GlobalConfigurationProperty configurationProperty) {
		this.configurationProperty = configurationProperty;
	}

}
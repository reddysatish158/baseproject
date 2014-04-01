package org.mifosplatform.billing.contract.data;

import java.util.List;

import org.mifosplatform.billing.contract.data.PeriodData;

public class SubscriptionData {

	private final Long id;

	private final String subscriptionPeriod;
	private final Long subscriptiontypeId;
    private final String Contractdata;
	private final String subscriptionType;
	private List<PeriodData> allowedperiods;

	private final Long units;
	

	public SubscriptionData(final Long id, final String subscriptionPeriod,
			final String subscriptionType, final Long units,final Long subscriptiontypeid,final String day_name) {

		this.id = id;
		this.subscriptionPeriod = subscriptionPeriod;
		this.subscriptionType = subscriptionType;
		this.units = units;
		this.subscriptiontypeId=subscriptiontypeid;
	
		this.Contractdata=null;
	}

	public SubscriptionData(final Long id,final String data, String subscriptionType)
	{
		this.Contractdata=data;
		this.id=id;
		this.subscriptionPeriod=null;
		this.subscriptionType=subscriptionType;
		this.units=null;
		this.subscriptiontypeId=null;
		

	}
	

	public SubscriptionData(List<PeriodData> datas, SubscriptionData products) {
		this.id = products.getId();
		this.allowedperiods = datas;
		this.subscriptionPeriod = products.getSubscriptionPeriod();
		this.units = products.getUnits();
		this.subscriptionType = products.getSubscriptionType();
        this.subscriptiontypeId=products.getSubscriptiontypeId();
        this.Contractdata=null;
	}

	public SubscriptionData(List<PeriodData> allowedtypes) {
		this.allowedperiods = allowedtypes;
		this.id=null;
		this.subscriptionPeriod=null;
		this.subscriptionType=null;
		this.units=null;
		this.subscriptiontypeId=null;
		this.Contractdata=null;
	}

	public List<PeriodData> getAllowedperiods() {
		return allowedperiods;
	}

	public void setAllowedperiods(List<PeriodData> allowedperiods) {
		this.allowedperiods = allowedperiods;
	}

	public Long getId() {
		return id;
	}

	public String getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public Long getUnits() {
		return units;
	}

	public Long getSubscriptiontypeId() {
		return subscriptiontypeId;
	}

	public String getContractdata() {
		return Contractdata;
	}


}

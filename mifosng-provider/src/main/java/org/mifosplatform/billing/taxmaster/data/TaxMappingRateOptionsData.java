package org.mifosplatform.billing.taxmaster.data;

import java.util.Collection;

import org.mifosplatform.billing.chargecode.data.ChargeCodeTemplateData;
import org.mifosplatform.billing.discountmaster.commands.DiscountValues;

public class TaxMappingRateOptionsData {
	private Collection<DiscountValues> datass;
	private Collection<TaxMasterData> taxMasterData;
	private Collection<ChargeCodeTemplateData> chargeCodeData;

	public TaxMappingRateOptionsData(Collection<ChargeCodeTemplateData> chargeCodeData,Collection<TaxMasterData> taxMasterData,Collection<DiscountValues> datass)
	{
		this.taxMasterData=taxMasterData;
		this.chargeCodeData=chargeCodeData;
		this.datass=datass;

	}
}

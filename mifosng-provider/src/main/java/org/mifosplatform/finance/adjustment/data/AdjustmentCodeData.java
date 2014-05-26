package org.mifosplatform.finance.adjustment.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.commands.DiscountValues;

public class AdjustmentCodeData {

	private final List<AdjustmentData> data;
	private final LocalDate adjustment_date;
	private List<DiscountValues> discountOptions;

	public AdjustmentCodeData(List<AdjustmentData> data) {
		this.data=data;
		this.adjustment_date=new LocalDate();
		this.discountOptions=setadjustment_type();
	}

	public List<AdjustmentData> getData() {
		return data;
	}

	public LocalDate getStartDate() {
		return adjustment_date;
	}
	
	public List<DiscountValues> setadjustment_type() {

		discountOptions = new ArrayList<DiscountValues>();
		discountOptions.add(new DiscountValues("CREDIT"));
		discountOptions.add(new DiscountValues("DEBIT"));

		return discountOptions;
	}


}

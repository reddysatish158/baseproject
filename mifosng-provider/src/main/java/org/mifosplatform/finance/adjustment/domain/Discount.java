package org.mifosplatform.finance.adjustment.domain;



import java.util.ArrayList;

import java.util.List;



public class Discount {

	private List<DiscountValues> discountOptions;

	public void setdiscount1() {

		discountOptions = new ArrayList<DiscountValues>();
		discountOptions.add(new DiscountValues("percentage"));
		discountOptions.add(new DiscountValues("flatamount"));

	}
	public void setadjustment_type() {

		discountOptions = new ArrayList<DiscountValues>();
		discountOptions.add(new DiscountValues("CREDIT"));
		discountOptions.add(new DiscountValues("DEBIT"));

	}

}


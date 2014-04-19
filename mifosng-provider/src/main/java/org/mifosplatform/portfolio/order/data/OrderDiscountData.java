package org.mifosplatform.portfolio.order.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class OrderDiscountData {
	
	private final Long id;
	private final Long priceId;
	private final String discountcode;
	private final String discountdescription;
	private final String discountType;
	private final BigDecimal discountAmount;
	private final LocalDate discountstartDate;
	private final LocalDate discountEndDate;

	public OrderDiscountData(Long id, Long priceId, String discountCode,String discountdescription, 
			BigDecimal discountAmount,String discountType, LocalDate startDate, LocalDate endDate) {
		
		this.id=id;
		this.priceId=priceId;
		this.discountcode=discountCode;
		this.discountdescription=discountdescription;
		this.discountAmount=discountAmount;
		this.discountType=discountType;
		this.discountstartDate=startDate;
		this.discountEndDate=endDate;
	}

	public Long getId() {
		return id;
	}

	public Long getPriceId() {
		return priceId;
	}

	public String getDiscountcode() {
		return discountcode;
	}

	public String getDiscountdescription() {
		return discountdescription;
	}

	public String getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public LocalDate getDiscountstartDate() {
		return discountstartDate;
	}

	public LocalDate getDiscountEndDate() {
		return discountEndDate;
	}

	
}

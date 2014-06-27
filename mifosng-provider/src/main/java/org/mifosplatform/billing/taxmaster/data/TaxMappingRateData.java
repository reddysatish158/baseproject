package org.mifosplatform.billing.taxmaster.data;

import java.math.BigDecimal;
import java.util.Date;

public class TaxMappingRateData {

	private Long id;
	private String chargeCode;
	private String taxCode;
	private Date startDate;
	private BigDecimal rate;
	private String taxType;
	private String  taxExemption;

	public TaxMappingRateData(final Long id,final String chargeCode,final String taxCode,
			final Date startDate,final BigDecimal rate, String taxType) {

		this.id =  id;
		this.chargeCode = chargeCode;
		this.taxCode = taxCode;
		this.startDate = startDate;
		this.rate = rate;
		this.taxType=taxType;

	}

	public TaxMappingRateData(String taxExemption) {
		this.taxExemption=taxExemption;
			}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getTaxType() {
		return taxType;
	}

	public String getTaxExemption() {
		return taxExemption;
	}

	public void setTaxExemption(String taxExemption) {
		this.taxExemption = taxExemption;
	}

}

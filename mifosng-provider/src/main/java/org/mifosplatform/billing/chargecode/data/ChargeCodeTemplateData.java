package org.mifosplatform.billing.chargecode.data;

public class ChargeCodeTemplateData {
private Long id;
private String chargeCode;
public ChargeCodeTemplateData(Long id,String chargeCode)
{
	this.id=id;
	this.chargeCode=chargeCode;

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

}

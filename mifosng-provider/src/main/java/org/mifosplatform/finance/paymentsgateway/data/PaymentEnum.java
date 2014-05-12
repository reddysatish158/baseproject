package org.mifosplatform.finance.paymentsgateway.data;


public enum PaymentEnum {

	FINISHED("FINISHED","Finished"), INVALID("INVALID","Invalid");

	private final String code;
	private final String value;

	private PaymentEnum(final String value,final String code) {
		this.code=code;
		this.value = value;
	}

	
	public String getCode() {
		return code;
	}


	public String getValue() {
		return this.value;
	}
	
	public static PaymentEnum fromInt(final Integer frequency) {

		PaymentEnum addressEnum = PaymentEnum.INVALID;
		switch (frequency) {
		case 1:
			addressEnum = PaymentEnum.FINISHED;
			break;
	
		default:
			addressEnum = PaymentEnum.INVALID;
			break;
		}
		return addressEnum;
	}
}

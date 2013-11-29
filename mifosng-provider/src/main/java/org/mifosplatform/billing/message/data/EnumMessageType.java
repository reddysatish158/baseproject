package org.mifosplatform.billing.message.data;

import org.mifosplatform.billing.media.domain.MediaEnum;

public enum EnumMessageType {

	EMAIL("E","Email"), Message("M","Message"), INVALID("N","invalid");

	private final String code;
	private final String value;

	private EnumMessageType(final String value,final String code) {
		this.code=code;
		this.value = value;
	}

	
	public String getCode() {
		return code;
	}


	public String getValue() {
		return this.value;
	}
	
	public static EnumMessageType fromInt(final Integer frequency) {

		EnumMessageType addressEnum = EnumMessageType.INVALID;
		switch (frequency) {
		case 1:
			addressEnum = EnumMessageType.EMAIL;
			break;
		case 2:
			addressEnum = EnumMessageType.Message;
			break;
	
		default:
			addressEnum = EnumMessageType.INVALID;
			break;
		}
		return addressEnum;
	}
}

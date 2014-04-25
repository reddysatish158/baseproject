package org.mifosplatform.organisation.message.data;


public enum EnumMessageType {

	EMAIL("E","Email"), Message("M","Message"),OSDMESSAGE("O","OSDMessage"), INVALID("N","invalid");

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
			
		case 3:
			addressEnum = EnumMessageType.OSDMESSAGE;
			break;
	
		default:
			addressEnum = EnumMessageType.INVALID;
			break;
		}
		return addressEnum;
	}
}

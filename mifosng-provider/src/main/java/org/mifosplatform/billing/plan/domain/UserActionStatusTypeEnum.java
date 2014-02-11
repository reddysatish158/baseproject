package org.mifosplatform.billing.plan.domain;


public enum UserActionStatusTypeEnum {

	ACTIVATION(1, "CategoryType.active"), //
	DISCONNECTION(2, "CategoryType.disconnected"),
	RECONNECTION(3,"CategoryType.reconnection"),
	MESSAGE(4,"CategoryType.message"),
	RENEWAL_BEFORE_AUTOEXIPIRY(5,"CategoryType.renewal before autoexipiry"),
	RENEWAL_AFTER_AUTOEXIPIRY(6,"CategoryType.renewal after autoexipiry"),
	DEVICE_SWAP(7,"CategoryType.device swap"),
	CHANGE_PLAN(8,"CategoryType.change plan"),
	INVALID(9, "CategoryType.invalid");


    private final Integer value;
	private final String code;

    private UserActionStatusTypeEnum(final Integer value, final String code) {
        this.value = value;
		this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

	public String getCode() {
		return code;
	}

	public static UserActionStatusTypeEnum fromInt(final Integer frequency) {

		UserActionStatusTypeEnum actionStatusTypeEnum = UserActionStatusTypeEnum.INVALID;
		switch (frequency) {
		case 1:
			actionStatusTypeEnum = UserActionStatusTypeEnum.ACTIVATION;
			break;
		case 2:
			actionStatusTypeEnum = UserActionStatusTypeEnum.DISCONNECTION;
			break;

		case 3:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RECONNECTION;
			break;
		
		case 4:
			actionStatusTypeEnum = UserActionStatusTypeEnum.MESSAGE;
			
		case 5:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY;
			
		case 6:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY;
			
		case 7:
			actionStatusTypeEnum = UserActionStatusTypeEnum.DEVICE_SWAP;
		
		case 8:
			actionStatusTypeEnum = UserActionStatusTypeEnum.CHANGE_PLAN;
			
		default:
			actionStatusTypeEnum = UserActionStatusTypeEnum.INVALID;
			break;
		}
		return actionStatusTypeEnum;
	}
}

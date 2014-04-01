package org.mifosplatform.billing.order.data;



import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class UserActionStatusEnumaration {

	public static EnumOptionData OrderStatusType(final int id) {
		return OrderStatusType(UserActionStatusTypeEnum.fromInt(id));
	}

	public static EnumOptionData OrderStatusType(final UserActionStatusTypeEnum type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case ACTIVATION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.ACTIVATION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.ACTIVATION  .getCode(), "ACTIVATION");
			break;
		case RECONNECTION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RECONNECTION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RECONNECTION.getCode(), "RECONNECTION");
			break;

		case DISCONNECTION:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.DISCONNECTION.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.DISCONNECTION.getCode(), "DISCONNECTION");
			break;
			
		case RENEWAL_AFTER_AUTOEXIPIRY:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY.getCode(), "RENEWAL AFTER AUTOEXIPIRY");
			break;
			
		case RENEWAL_BEFORE_AUTOEXIPIRY:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.getCode(), "RENEWAL BEFORE AUTOEXIPIRY");
			break;		
	
		case DEVICE_SWAP:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.DEVICE_SWAP.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.DEVICE_SWAP.getCode(), "DEVICE SWAP");
			break;	
		
		case CHANGE_PLAN:
			optionData = new EnumOptionData(UserActionStatusTypeEnum.CHANGE_PLAN.getValue().longValue(), codePrefix + UserActionStatusTypeEnum.CHANGE_PLAN.getCode(), "CHANGE PLAN");
			break;		
			
		
		default:
			optionData = new EnumOptionData(StatusTypeEnum.INVALID.getValue().longValue(), StatusTypeEnum.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}

}

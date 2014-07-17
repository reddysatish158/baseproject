package org.mifosplatform.portfolio.order.data;



import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.plan.domain.StatusTypeEnum;

public class OrderStatusEnumaration {

	public static EnumOptionData OrderStatusType(final int id) {
		return OrderStatusType(StatusTypeEnum.fromInt(id));
	}

	public static EnumOptionData OrderStatusType(final StatusTypeEnum type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case ACTIVE:
			optionData = new EnumOptionData(StatusTypeEnum.ACTIVE.getValue().longValue(), codePrefix + StatusTypeEnum.ACTIVE.getCode(), "ACTIVE");
			break;
		case INACTIVE:
			optionData = new EnumOptionData(StatusTypeEnum.INACTIVE.getValue().longValue(), codePrefix + StatusTypeEnum.INACTIVE.getCode(), "INACTIVE");
			break;

		case DISCONNECTED:
			optionData = new EnumOptionData(StatusTypeEnum.DISCONNECTED.getValue().longValue(), codePrefix + StatusTypeEnum.DISCONNECTED.getCode(), "DISCONNECTED");
			break;
			
		case PENDING:
			optionData =new EnumOptionData(StatusTypeEnum.PENDING.getValue().longValue(),codePrefix+StatusTypeEnum.PENDING,"PENDING");
			break;
			
		case TERMINATED:
			optionData =new EnumOptionData(StatusTypeEnum.TERMINATED.getValue().longValue(),codePrefix+StatusTypeEnum.TERMINATED,"TERMINATED");
			break;	

		default:
			optionData = new EnumOptionData(StatusTypeEnum.INVALID.getValue().longValue(), StatusTypeEnum.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}

}

package org.mifosplatform.cms.media.domain;
import org.mifosplatform.finance.paymentsgateway.data.PaymentEnum;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.organisation.message.data.EnumMessageType;

public class MediaTypeEnumaration {
	public static MediaEnumoptionData enumOptionData(final int id) {

		return enumOptionData(MediaEnum.fromInt(id));
	}

	public static MediaEnumoptionData enummessageData(final int id) {
		return enummessageData(EnumMessageType.fromInt(id));
	}

	public static MediaEnumoptionData enumOptionData(final MediaEnum mediaEnum) {
		final String codePrefix = "deposit.interest.compounding.period.";
		MediaEnumoptionData optionData = null;

		switch (mediaEnum) {
		case MOVIES:
			optionData = new MediaEnumoptionData(MediaEnum.MOVIES.getValue(), codePrefix + MediaEnum.MOVIES.getCode(), "MOVIES");
			break;

		case TV_SERIALS:
			optionData = new MediaEnumoptionData(MediaEnum.TV_SERIALS.getValue(), codePrefix + MediaEnum.TV_SERIALS.getCode(), "TV SERIALS");
			break;

		case COMING_SOON:
			optionData = new MediaEnumoptionData(MediaEnum.COMING_SOON.getValue(), codePrefix + MediaEnum.COMING_SOON.getCode(), "COMING SOON");
			break;

		default:
			optionData = new MediaEnumoptionData(MediaEnum.INVALID.getValue(), codePrefix + MediaEnum.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}
	public static MediaEnumoptionData enummessageData(final EnumMessageType mediaEnum) {

		
		MediaEnumoptionData optionData = null;
		switch (mediaEnum) {
		case EMAIL:
			optionData = new MediaEnumoptionData(EnumMessageType.EMAIL.getValue(), EnumMessageType.EMAIL.getCode(), "EMAIL");
			break;
		
		case Message:
			optionData = new MediaEnumoptionData(EnumMessageType.Message.getValue(), EnumMessageType.Message.getCode(), "MESSAGE");
			break;
		
		case OSDMESSAGE:
			optionData = new MediaEnumoptionData(EnumMessageType.OSDMESSAGE.getValue(), EnumMessageType.OSDMESSAGE.getCode(), "OSDMESSAGE");
			break;	

		default:
			optionData = new MediaEnumoptionData(MediaEnum.INVALID.getValue(),  MediaEnum.INVALID.getCode(), "INVALID");
			break;
		}

		return optionData;

	}


}


package org.mifosplatform.billing.emun.service;

import java.util.Collection;

import org.mifosplatform.billing.emun.data.EnumValuesData;

public interface EnumReadplaformService {

	public Collection<EnumValuesData> getEnumValues(final String codeName);

}

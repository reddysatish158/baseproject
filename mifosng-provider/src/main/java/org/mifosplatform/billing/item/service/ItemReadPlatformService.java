package org.mifosplatform.billing.item.service;

import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.item.data.ChargesData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface ItemReadPlatformService {

	List<EnumOptionData> retrieveItemClassType();

	List<EnumOptionData> retrieveUnitTypes();

	List<ChargesData> retrieveChargeCode();

	List<ItemData> retrieveAllItems();

	ItemData retrieveSingleItemDetails(Long itemId);

	Page<ItemData> retrieveAllItems(SearchSqlQuery searchItems);

}

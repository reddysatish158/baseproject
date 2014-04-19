/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.smartsearch.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.accounting.journalentry.api.DateParam;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.smartsearch.data.SmartSearchData;
import org.mifosplatform.organisation.smartsearch.service.SmartSearchReadplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/smartsearch")
@Component
@Scope("singleton")
public class SmartSearchApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "clientId", "clientName",
            "paymentDate","paymentType","receiptNo","amount"));

    private final String resourceNameForPermission = "SMARTSEARCH";

    private final SmartSearchReadplatformService smartSearchReadplatformService;
    private final DefaultToApiJsonSerializer<SmartSearchData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    

    @Autowired
    public SmartSearchApiResource(final PlatformSecurityContext context,final SmartSearchReadplatformService smartSearchReadplatformService,
            final DefaultToApiJsonSerializer<SmartSearchData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper) {
    	
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.smartSearchReadplatformService=smartSearchReadplatformService;
        this.apiJsonSerializerService = toApiJsonSerializer;
        
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllJournalEntries(@Context final UriInfo uriInfo, @QueryParam("searchText") final String searchText,
            @QueryParam("fromDate") final DateParam fromDateParam, @QueryParam("toDate") final DateParam toDateParam,
            @QueryParam("limit") final Integer limit,@QueryParam("offset") final Integer offset) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        // get dates from date params
        Date fromDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDate();
        }
        Date toDate = null;
        if (toDateParam != null) {
            toDate = toDateParam.getDate();
        }
        Page<SmartSearchData> smartSearchDatas=this.smartSearchReadplatformService.retrieveAllSearchData(searchText, fromDate, toDate,limit,offset);
		return this.apiJsonSerializerService.serialize(smartSearchDatas);
    }

    

  
}
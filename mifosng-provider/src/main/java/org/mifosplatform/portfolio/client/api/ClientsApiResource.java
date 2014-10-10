/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.cms.mediadetails.exception.NoMediaDeviceFoundException;
import org.mifosplatform.cms.mediadevice.data.MediaDeviceData;
import org.mifosplatform.cms.mediadevice.service.MediaDeviceReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clients")
@Component
@Scope("singleton")
public class ClientsApiResource {

    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
    private final MediaDeviceReadPlatformService mediaDeviceReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final AddressReadPlatformService addressReadPlatformService;
    private final AllocationReadPlatformService allocationReadPlatformService;
    private final GlobalConfigurationRepository configurationRepository;

    @Autowired
    public ClientsApiResource(final PlatformSecurityContext context, final ClientReadPlatformService readPlatformService,
            final OfficeReadPlatformService officeReadPlatformService, final ToApiJsonSerializer<ClientData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,AddressReadPlatformService addressReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final AllocationReadPlatformService allocationReadPlatformService,
            final GlobalConfigurationRepository configurationRepository,final MediaDeviceReadPlatformService deviceReadPlatformService) {
        this.context = context;
        this.clientReadPlatformService = readPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.addressReadPlatformService=addressReadPlatformService;
        this.allocationReadPlatformService=allocationReadPlatformService;
        this.configurationRepository=configurationRepository;
        this.mediaDeviceReadPlatformService=deviceReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo,  @QueryParam("commandParam") final String commandParam) {

        context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        ClientData clientData = this.clientReadPlatformService.retrieveTemplate();
        if (is(commandParam, "close")) {
        	
        clientData = this.clientReadPlatformService.retrieveAllClosureReasons(ClientApiConstants.CLIENT_CLOSURE_REASON);
        }
        GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName("LOGIN");
        clientData.setConfigurationProperty(configurationProperty);
        clientData=handleAddressTemplateData(clientData);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    private ClientData handleAddressTemplateData(ClientData clientData) {
    	 List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
         List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
         List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
         List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
         AddressData data=new AddressData(null,countryData,statesData,citiesData,enumOptionDatas);
         clientData.setAddressTemplate(data);
         return clientData;
	}

	@GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("officeId") final Long officeId,
            @QueryParam("externalId") final String externalId, @QueryParam("displayName") final String displayName,
            @QueryParam("firstName") final String firstname, @QueryParam("lastName") final String lastname,
            @QueryParam("underHierarchy") final String hierarchy, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder) {

        context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final SearchParameters searchParameters = SearchParameters.forClients(sqlSearch, officeId, externalId, displayName, firstname,
                lastname, hierarchy, offset, limit, orderBy, sortOrder);
        final Page<ClientData> clientData = this.clientReadPlatformService.retrieveAll(searchParameters);
        return this.toApiJsonSerializer.serialize(clientData);
    }

    @GET
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
        ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
        String balanceCheck="N";
        if(configurationProperty.isEnabled()){
        	balanceCheck="Y";
        }

        if (settings.isTemplate()) {
            final List<OfficeData> allowedOffices = new ArrayList<OfficeData>(officeReadPlatformService.retrieveAllOfficesForDropdown());
            final Collection<ClientCategoryData> categoryDatas=this.clientReadPlatformService.retrieveClientCategories();
            List<String> allocationDetailsDatas=this.allocationReadPlatformService.retrieveHardWareDetails(clientId);
            clientData = ClientData.templateOnTop(clientData, allowedOffices,categoryDatas,allocationDetailsDatas,null);

        }else{
        	 List<String> allocationDetailsDatas=this.allocationReadPlatformService.retrieveHardWareDetails(clientId);
             clientData = ClientData.templateOnTop(clientData, null,null,allocationDetailsDatas,balanceCheck);
        }
        
        GlobalConfigurationProperty paypalconfigurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IS_PAYPAL_CHECK);
        clientData.setConfigurationProperty(paypalconfigurationProperty);
        GlobalConfigurationProperty paypalconfigurationPropertyForIos=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IS_PAYPAL_CHECK_IOS);
        clientData.setConfigurationPropertyForIos(paypalconfigurationPropertyForIos);
        
        return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createClient() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateClient(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("clientId") final Long clientId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteClient(clientId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String activate(@PathParam("clientId") final Long clientId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        if (is(commandParam, "activate")) {
            final CommandWrapper commandRequest = builder.activateClient(clientId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        }else  if (is(commandParam, "close")) {

        	 final CommandWrapper commandRequest = new CommandWrapperBuilder() //
             .deleteClient(clientId) //
             .withJson(apiRequestBodyAsJson) //
             .build(); //


           result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        if (result == null) {
            throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "activate" });
        }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
  
}
package org.mifosplatform.billing.servicemapping.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.servicemapping.data.ServiceCategoryData;
import org.mifosplatform.billing.servicemapping.data.ServiceCodeData;
import org.mifosplatform.billing.servicemapping.data.ServiceMappingData;
import org.mifosplatform.billing.servicemapping.service.ServiceMappingReadPlatformService;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("servicecategory")
@Component
@Scope("singleton")
public class ServiceCategoryApiResource {
	
	private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id","serviceId","category","language","isdeleted"));
	private final String resourceNameForPermissions = "SERVICECATEGORY";
	
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final ServiceMappingReadPlatformService serviceMappingReadPlatformService;
	private final DefaultToApiJsonSerializer<ServiceCategoryData> toApiJsonSerializer;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PlatformSecurityContext context;
	public static String SERVICE_LANG="Asset language";
	public static String SERVICE_CATEGORY="Service Category";
	
@Autowired
public ServiceCategoryApiResource(final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
		final DefaultToApiJsonSerializer<ServiceCategoryData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
		final PlatformSecurityContext context,final ServiceMappingReadPlatformService serviceMappingReadPlatformService,
		final CodeValueReadPlatformService codeValueReadPlatformService){
	
	this.commandSourceWritePlatformService=commandSourceWritePlatformService;
	this.serviceMappingReadPlatformService=serviceMappingReadPlatformService;
	this.codeValueReadPlatformService=codeValueReadPlatformService;
	this.apiRequestParameterHelper=apiRequestParameterHelper;
	this.toApiJsonSerializer=toApiJsonSerializer;
	this.context=context;
	
}


@GET
@Path("template")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public String getTemplateData(@Context final UriInfo uriInfo){
	
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	
	ServiceCategoryData categoryData=null;
	categoryData=handleTemplateData(categoryData);
	

	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	return this.toApiJsonSerializer.serialize(settings, categoryData, RESPONSE_PARAMETERS);
	
}


private ServiceCategoryData handleTemplateData(ServiceCategoryData categoryData) {
	
	List<ServiceCodeData> serviceCodeData = this.serviceMappingReadPlatformService.getServiceCode();
	Collection<CodeValueData> serviceCategeries =this.codeValueReadPlatformService.retrieveCodeValuesByCode(SERVICE_CATEGORY);
	Collection<CodeValueData> languagies =this.codeValueReadPlatformService.retrieveCodeValuesByCode(SERVICE_CATEGORY);
	if(categoryData == null){
	return new ServiceCategoryData(serviceCodeData,serviceCategeries,languagies);
	}else{
		categoryData=new ServiceCategoryData(categoryData,serviceCodeData,serviceCategeries,languagies);
	}
}

}

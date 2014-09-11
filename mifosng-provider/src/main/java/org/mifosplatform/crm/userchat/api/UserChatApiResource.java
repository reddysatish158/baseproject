package org.mifosplatform.crm.userchat.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.userchat.data.UserChatData;
import org.mifosplatform.crm.userchat.service.UserChatReadplatformReadService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("userchats")
@Component
@Scope("singleton")
public class UserChatApiResource{

	   private final Set<String> USER_CHAT_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "userName", "messageDate", "message","createdByUser"));
 
	    private final String resourceNameForPermissions = "USERCHATMESSAGE";
	    private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<UserChatData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final UserChatReadplatformReadService  userChatReadplatformReadService;
	    private final AppUserReadPlatformService appUserReadPlatformService;

	    @Autowired
	    public UserChatApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer<UserChatData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final UserChatReadplatformReadService  chatReadplatformReadService,final AppUserReadPlatformService appUserReadPlatformService) {

	    	this.context = context;
	        this.toApiJsonSerializer = toApiJsonSerializer;
	        this.apiRequestParameterHelper = apiRequestParameterHelper;
	        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	        this.userChatReadplatformReadService=chatReadplatformReadService;
	        this.appUserReadPlatformService=appUserReadPlatformService;
	    }
	    @POST
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String addNewMessage(final String apiRequestBodyAsJson) {

	        final CommandWrapper commandRequest = new CommandWrapperBuilder().createUserChat().withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	    }
	    
		
	    @GET
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveInboxMessageDetails(@Context final UriInfo uriInfo) {
	  
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    
	    final List<UserChatData> userChatDatas = this.userChatReadplatformReadService.getUserChatDetails();
	    final Collection<AppUserData> appUserDatas = this.appUserReadPlatformService.retrieveAllUsers();
	    Collection<AppUserData> userDatas=new ArrayList<AppUserData>();

	        for(AppUserData appUserData:appUserDatas ){
	        	 if(!appUserData.username().equalsIgnoreCase(context.authenticatedUser().getUsername())){
	        		 userDatas.add(appUserData);
	        	 }
	           }
	           UserChatData data=new UserChatData(userChatDatas,userDatas);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, USER_CHAT_DATA_PARAMETERS);
		    }
	    
	    @GET
	    @Path("sentmessages")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSentMessageDetails(@Context final UriInfo uriInfo) {

	    context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    final List<UserChatData> userChatDatas = this.userChatReadplatformReadService.getUserSentMessageDetails();
	    final Collection<AppUserData> appUserDatas = this.appUserReadPlatformService.retrieveAllUsers();
	    Collection<AppUserData> userDatas=new ArrayList<AppUserData>();

	        for(AppUserData appUserData:appUserDatas ){
	        	 if(!appUserData.username().equalsIgnoreCase(context.authenticatedUser().getUsername())){
	        		 userDatas.add(appUserData);
	        	 }
	           }
	           UserChatData data=new UserChatData(userChatDatas,userDatas);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, USER_CHAT_DATA_PARAMETERS);
		    }
	    
	    @PUT
	    @Path("{messageId}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String updateMessageasRead(@PathParam("messageId") final Long messageId,final String apiRequestBodyAsJson) {

	        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateUsermessage(messageId).withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	    }
	    
	    @DELETE
	    @Path("{messageId}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String deleteUserChatMessage(@PathParam("messageId") final Long meesageId) {

	        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteUserChatmessage(meesageId).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	    } 
}

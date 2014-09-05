/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.crm.userchat.service.UserChatReadplatformReadService;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.data.AuthenticatedUserData;
import org.mifosplatform.useradministration.data.RoleData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.useradministration.service.RoleReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.util.Base64;

@Path("/authentication")
@Component
@Scope("singleton")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
    private final RoleReadPlatformService roleReadPlatformService;
    private final UserChatReadplatformReadService userChatReadplatformReadService;
    private final LoginHistoryRepository loginHistoryRepository;
    
    @Autowired
    public AuthenticationApiResource(
            @Qualifier("customAuthenticationProvider") final DaoAuthenticationProvider customAuthenticationProvider,
            final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService, final RoleReadPlatformService roleReadPlatformService,
            final  UserChatReadplatformReadService userChatReadplatformReadService,final LoginHistoryRepository loginHistoryRepository) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.roleReadPlatformService = roleReadPlatformService;
        this.userChatReadplatformReadService=userChatReadplatformReadService;
        this.loginHistoryRepository=loginHistoryRepository;
    }

    @POST
   
    @Produces({ MediaType.APPLICATION_JSON })
    public String authenticate(@QueryParam("username") final String username, @QueryParam("password") final String password,
    		@Context HttpServletRequest req) {
    	
    	String ipAddress = req.getRemoteHost();
    	String session = req.getSession().getId();
    	int maxTime=req.getSession().getMaxInactiveInterval();
    	
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticationCheck = customAuthenticationProvider.authenticate(authentication);

        Collection<String> permissions = new ArrayList<String>();
        AuthenticatedUserData authenticatedUserData = new AuthenticatedUserData(username, permissions);
        if (req.getSession().isNew()) { 
    	    LoginHistory loginHistory=new LoginHistory(ipAddress,null,session,new Date(),null,username,"ACTIVE");
    		this.loginHistoryRepository.save(loginHistory);
    		Long loginHistoryId=loginHistory.getId();
    		req.getSession().setAttribute("lId", loginHistoryId);
        }

        if (authenticationCheck.isAuthenticated()) {
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(authenticationCheck.getAuthorities());
            for (GrantedAuthority grantedAuthority : authorities) {
                permissions.add(grantedAuthority.getAuthority());
            }
            AppUser principal = (AppUser) authenticationCheck.getPrincipal();
            byte[] base64EncodedAuthenticationKey = Base64.encode(username + ":" + password);

            Collection<RoleData> roles = this.roleReadPlatformService.retrieveAll();
            Long unreadMessages=this.userChatReadplatformReadService.getUnreadMessages(username);

            authenticatedUserData = new AuthenticatedUserData(username, roles, permissions, principal.getId(), new String(
                    base64EncodedAuthenticationKey),unreadMessages,ipAddress,session,maxTime,(Long)req.getSession().getAttribute("lId"));
        }

        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}
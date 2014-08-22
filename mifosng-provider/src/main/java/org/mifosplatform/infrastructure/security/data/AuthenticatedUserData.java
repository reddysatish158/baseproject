/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.data;

import java.util.Collection;

import org.mifosplatform.useradministration.data.RoleData;

/**
 * Immutable data object for authentication.
 */
public class AuthenticatedUserData {

    @SuppressWarnings("unused")
    private final String username;
    @SuppressWarnings("unused")
    private final Long userId;
    @SuppressWarnings("unused")
    private final String base64EncodedAuthenticationKey;
    @SuppressWarnings("unused")
    private final boolean authenticated;
    @SuppressWarnings("unused")
    private final Collection<RoleData> roles;
    @SuppressWarnings("unused")
    private final Collection<String> permissions;
    
    @SuppressWarnings("unused")
    private final Long unReadMessages;
    @SuppressWarnings("unused")
    private final String ipAddress;
    @SuppressWarnings("unused")
    private final String session;
    @SuppressWarnings("unused")
    private final Integer maxTime;
    @SuppressWarnings("unused")
    private final Long loginHistoryId;

    public AuthenticatedUserData(final String username, final Collection<String> permissions) {
        this.username = username;
        this.userId = null;
        this.base64EncodedAuthenticationKey = null;
        this.authenticated = false;
        this.roles = null;
        this.permissions = permissions;
        this.unReadMessages=null;
        this.ipAddress=null;
        this.session=null;
        this.maxTime=null;
        this.loginHistoryId=null;
    }

    public AuthenticatedUserData(final String username, final Collection<RoleData> roles, final Collection<String> permissions,
            final Long userId, final String base64EncodedAuthenticationKey,final Long unreadMessages,final String remoteHost,
            final String session, int maxTime,Long loginHistoryId) {
        this.username = username;
        this.userId = userId;
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
        this.authenticated = true;
        this.roles = roles;
        this.permissions = permissions;
        this.unReadMessages=unreadMessages;
        this.ipAddress = remoteHost;
        this.session = session;
        this.maxTime = maxTime;
        this.loginHistoryId=loginHistoryId;
    }
}
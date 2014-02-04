/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.mifosplatform.infrastructure.cache.domain.CacheType;
import org.mifosplatform.infrastructure.cache.service.CacheWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.data.PlatformRequestLog;
import org.mifosplatform.infrastructure.security.exception.InvalidTenantIdentiferException;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.core.AuthenticationException;

/**
 * A customised version of spring security's {@link BasicAuthenticationFilter}.
 * 
 * This filter is responsible for extracting multi-tenant and basic auth
 * credentials from the request and checking that the details provided are
 * valid.
 * 
 * If multi-tenant and basic auth credentials are valid, the details of the
 * tenant are stored in {@link MifosPlatformTenant} and stored in a
 * {@link ThreadLocal} variable for this request using
 * {@link ThreadLocalContextUtil}.
 * 
 * If multi-tenant and basic auth credentials are invalid, a http error response
 * is returned.
 */
public class TenantAwareBasicAuthenticationFilter extends BasicAuthenticationFilter {

	 private static boolean firstRequestProcessed = false;
     private final static Logger logger = LoggerFactory.getLogger(TenantAwareBasicAuthenticationFilter.class);

    //ashok changed
    private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private RememberMeServices rememberMeServices = new NullRememberMeServices();
   
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AuthenticationManager authenticationManager;
    private TenantDetailsService tenantDetailsService;
    private ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final ConfigurationDomainService configurationDomainService;
    private final CacheWritePlatformService cacheWritePlatformService;
    private String tenantRequestHeader = "X-Mifos-Platform-TenantId";
    private boolean exceptionIfHeaderMissing = true;

    @Autowired
    public TenantAwareBasicAuthenticationFilter(final AuthenticationManager authenticationManager,final AuthenticationEntryPoint authenticationEntryPoint,
    		final ConfigurationDomainService configurationDomainService,final CacheWritePlatformService cacheWritePlatformService,
    		final TenantDetailsService tenantDetailsService,final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer) {
        super(authenticationManager, authenticationEntryPoint);
        this.configurationDomainService=configurationDomainService;
        this.cacheWritePlatformService=cacheWritePlatformService;
        this.tenantDetailsService=tenantDetailsService;
        this.toApiJsonSerializer=toApiJsonSerializer;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path=request.getRequestURI();
        StopWatch task = new StopWatch();
        task.start();

        try {
        	String header = request.getHeader("Authorization");
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                // ignore to allow 'preflight' requests from AJAX applications
                // in different origin (domain name)
            	super.doFilter(req, res, chain); //ashok changed please comment when delete the else if statement
            }else if(path.contains("/api/v1/paymentgateways") && request.getMethod().equalsIgnoreCase("POST")){
            	
           	    String username= request.getParameter("username");
                String password= request.getParameter("password");
                
                final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");             	
	             ThreadLocalContextUtil.setTenant(tenant);
                
                if(!(org.apache.commons.lang.StringUtils.isBlank(username) || org.apache.commons.lang.StringUtils.isBlank(password))){
	            	
	                UsernamePasswordAuthenticationToken authRequest =
	                        new UsernamePasswordAuthenticationToken(username, password);
	                authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	                Authentication authResult = authenticationManager.authenticate(authRequest);
	       
	                SecurityContextHolder.getContext().setAuthentication(authResult);
	
	                rememberMeServices.loginSuccess(request, response, authResult);
	
	                onSuccessfulAuthentication(request, response, authResult);
	                chain.doFilter(request, response);
                }else{
               	   throw new AuthenticationCredentialsNotFoundException("Credentials are not valid");
                }
                
           }else if(path.contains("/api/v1/entitlements/get") && request.getMethod().equalsIgnoreCase("GET")){
           	
               final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");             	
	             ThreadLocalContextUtil.setTenant(tenant);
	             super.doFilter(req, res, chain);
               
          }else {

               String tenantId = request.getHeader(tenantRequestHeader);
               if (org.apache.commons.lang.StringUtils.isBlank(tenantId)) {
                   tenantId = request.getParameter("tenantIdentifier");
               }

               if (tenantId == null && exceptionIfHeaderMissing) { throw new InvalidTenantIdentiferException(
                       "No tenant identifier found: Add request header of '" + tenantRequestHeader
                               + "' or add the parameter 'tenantIdentifier' to query string of request URL."); }

               // check tenants database for tenantId
               final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById(tenantId);
	             ThreadLocalContextUtil.setTenant(tenant);
               if (!firstRequestProcessed) {
                   final boolean ehcacheEnabled = this.configurationDomainService.isEhcacheEnabled();
                   if (ehcacheEnabled) {
                       this.cacheWritePlatformService.switchToCache(CacheType.SINGLE_NODE);
                   } else {
                       this.cacheWritePlatformService.switchToCache(CacheType.NO_CACHE);
                   }
                   TenantAwareBasicAuthenticationFilter.firstRequestProcessed = true;
               }

               ThreadLocalContextUtil.setTenant(tenant);
               //ashokchanged
               super.doFilter(req, res, chain);
               //ashok changed
           }
            
               //     response.addHeader("Authorization", "YmlsbGluZzpwYXNzd29yZA==");
            //super.doFilter(req, res, chain); //active this line
        } catch (InvalidTenantIdentiferException e) {
            // deal with exception at low level
            SecurityContextHolder.getContext().setAuthentication(null);

            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "Mifos Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();

            rememberMeServices.loginFail(request, response);

            onUnsuccessfulAuthentication(request, response, failed);

           
                authenticationEntryPoint.commence(request, response, failed);
            
            return;
        } finally {
            task.stop();
            final PlatformRequestLog log = PlatformRequestLog.from(task, request);
            logger.info(toApiJsonSerializer.serialize(log));
        }
    }
}
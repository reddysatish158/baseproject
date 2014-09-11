/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.domain.EmailDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GmailBackedPlatformEmailService implements PlatformEmailService {
	 private final GlobalConfigurationRepository repository;
	 String mailId;
     String encodedPassword;
     String decodePassword;
     String hostName;
     @Autowired
     public GmailBackedPlatformEmailService(final GlobalConfigurationRepository repository) {
         this.repository = repository;
     }
    @Override
    public void sendToUserAccount(final EmailDetail emailDetail, final String unencodedPassword) {
        Email email = new SimpleEmail();
/*<<<<<<< HEAD

<<<<<<< HEAD
        String authuserName ="Open Billing System Community";
=======
        String authuserName = "Open Billing System Community";
>>>>>>> obsplatform-1.01

        String authuser = "kiran@hugotechnologies.com";//"info@openbillingsystem.com";
        String authpwd ="kirankiran"; //"openbs@13";

=======*/
        GlobalConfigurationProperty configuration=repository.findOneByName("SMTP");
        String value= configuration.getValue();
       
        try {
			JSONObject object =new JSONObject(value);
			mailId=(String) object.get("mailId");
			encodedPassword=(String) object.get("password");
			decodePassword=new String(Base64.decodeBase64(encodedPassword));
			hostName=(String) object.get("hostName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        String authuserName =mailId;// "Open Billing System Community";
        
        String authuser = mailId;//"info@openbillingsystem.com";
        String authpwd =decodePassword; //"openbs@13";
        
        // Very Important, Don't use email.setAuthentication()
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        email.setHostName(hostName);
        try {
            email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            email.setFrom(authuser, authuserName);

            StringBuilder subjectBuilder = new StringBuilder().append("BillingX Prototype Demo: ").append(emailDetail.getContactName()).append(" user account creation.");

            email.setSubject(subjectBuilder.toString());

            String sendToEmail = emailDetail.getAddress();

            StringBuilder messageBuilder = new StringBuilder().append("You are receiving this email as your email account: ").append(sendToEmail).append(" has being used to create a user account for an organisation named [").append(emailDetail.getOrganisationName()).append("] on BillingX Prototype Demo.").append("You can login using the following credentials: username: ").append(emailDetail.getUsername()).append(" password: ").append(unencodedPassword);

            email.setMsg(messageBuilder.toString());

            email.addTo(sendToEmail, emailDetail.getContactName());
            email.send();
        } catch (EmailException e) {
            throw new PlatformEmailSendException(e);
        }
    }
}
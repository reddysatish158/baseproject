/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
package org.mifosplatform.billing.message.service;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.billing.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.billing.message.domain.BillingMessage;
import org.mifosplatform.billing.message.domain.MessageDataRepository;
import org.mifosplatform.infrastructure.core.domain.EmailDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageGmailBackedPlatformEmailService implements MessagePlatformEmailService {

private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;
 private final MessageDataRepository messageDataRepository;

 @Autowired
    public MessageGmailBackedPlatformEmailService(BillingMesssageReadPlatformService billingMesssageReadPlatformService,
    		MessageDataRepository messageDataRepository){
	
	 this.billingMesssageReadPlatformService=billingMesssageReadPlatformService;
	 this.messageDataRepository=messageDataRepository;
	 
 }

 @Override
 public void sendToUserEmail() {
     Email email = new SimpleEmail();

     String authuserName = "info@hugotechnologies.com";
     //String authusername="hugotechnologies";

     String authuser = "ashokcse556@gmail.com";
     String authpwd = "9989720715";

     // Very Important, Don't use email.setAuthentication()
     email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
     email.setDebug(true); // true if you want to debug
     email.setHostName("smtp.gmail.com");
     try {
         email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
         email.setFrom(authuserName, authuser);
         List<BillingMessageDataForProcessing> billingMessageDataForProcessings=this.billingMesssageReadPlatformService.retrieveMessageDataForProcessing();
   	    for(BillingMessageDataForProcessing emailDetail : billingMessageDataForProcessings){
          
         StringBuilder subjectBuilder = new StringBuilder().append(" ").append(emailDetail.getSubject()).append("  ");

         email.setSubject(subjectBuilder.toString());
  
        String sendToEmail = emailDetail.getMessageTo();

         StringBuilder messageBuilder = new StringBuilder().append(emailDetail.getHeader()).append(".").append(emailDetail.getBody()).append(",").append(emailDetail.getFooter());

         email.setMsg(messageBuilder.toString());

         email.addTo(sendToEmail, emailDetail.getMessageFrom());
         email.setSmtpPort(587);
         email.send();
         BillingMessage billingMessage=this.messageDataRepository.findOne(emailDetail.getId());
         if(billingMessage.getStatus().contentEquals("N"))
         {
         	billingMessage.updateStatus();
         }
         this.messageDataRepository.save(billingMessage);
         
         
        }
   	 } catch (EmailException e) {
         throw new MessagePlatformEmailSendException(e);
     }
 }
}*/

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.billing.message.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.mifosplatform.billing.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.billing.message.domain.BillingMessage;
import org.mifosplatform.billing.message.domain.MessageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class MessageGmailBackedPlatformEmailService implements
		MessagePlatformEmailService {
	private final MessageDataRepository messageDataRepository;

	@Autowired
	public MessageGmailBackedPlatformEmailService(
			MessageDataRepository messageDataRepository) {

		this.messageDataRepository = messageDataRepository;

	}

	@Override
	public void sendToUserEmail(BillingMessageDataForProcessing emailDetail) {
		// TODO Auto-generated method stub
		// HtmlEmail email = new HtmlEmail();
		Email email = new SimpleEmail();
		String authuserName = "info@hugotechnologies.com";

		String authuser = "kiran@hugotechnologies.com";
		String authpwd = "kirankiran";

		// Very Important, Don't use email.setAuthentication()
		email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
		email.setDebug(false); // true if you want to debug
		email.setHostName("smtp.gmail.com");
		try {
			email.getMailSession().getProperties()
					.put("mail.smtp.starttls.enable", "true");
			email.setFrom(authuserName, authuser);
			// email.setSmtpPort(587);
			StringBuilder subjectBuilder = new StringBuilder().append(" ")
					.append(emailDetail.getSubject()).append("  ");

			email.setSubject(subjectBuilder.toString());

			String sendToEmail = emailDetail.getMessageTo();
			StringBuilder messageBuilder = new StringBuilder()
					.append(emailDetail.getHeader()).append(".")
					.append(emailDetail.getBody()).append(",")
					.append(emailDetail.getFooter());
			email.addTo(sendToEmail, emailDetail.getMessageFrom());
			// email.setHtmlMsg("<html>"+messageBuilder.toString()+"</html>");
			email.setMsg(messageBuilder.toString());
			email.send();
			BillingMessage billingMessage = this.messageDataRepository
					.findOne(emailDetail.getId());
			if (billingMessage.getStatus().contentEquals("N")) {
				billingMessage.updateStatus();
			}
			this.messageDataRepository.save(billingMessage);

		} catch (Exception e) {
			handleCodeDataIntegrityIssues(null, e);
		}

	}

	private void handleCodeDataIntegrityIssues(Object object, Exception dve) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendToUserMobile(String message, Long id) {
		// TODO Auto-generated method stub
		try {
			/*
			 * String retval = ""; postData += "User=" +
			 * URLEncoder.encode(User,"UTF-8") + "&passwd=" + passwd +
			 * "&mobilenumber=" + mobilenumber + "&message=" +
			 * URLEncoder.encode(message,"UTF-8") + "&sid=" + sid + "&mtype=" +
			 * mtype + "&DR=" + DR;
			 */

			String retval = "";
			URL url = new URL("http://smscountry.com/SMSCwebservice_Bulk.aspx");

			HttpURLConnection urlconnection = (HttpURLConnection) url
					.openConnection();

			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(
					urlconnection.getOutputStream());
			out.write(message);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlconnection.getInputStream()));
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				retval += decodedString;
			}
			in.close();
			System.out.println(retval);
			String k = retval.substring(0, 3);

			if (k.equalsIgnoreCase("OK:")) {
				BillingMessage billingMessage = this.messageDataRepository
						.findOne(id);
				if (billingMessage.getStatus().contentEquals("N")) {
					billingMessage.updateStatus();
				}
				this.messageDataRepository.save(billingMessage);
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException : "
					+ e.getMessage() + " . encoding pattern not supported.");
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException : " + e.getMessage()
					+ " . URL is not located.");
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage() + ".");
		}
	}
}

/*
 * Email email = new SimpleEmail();
 * 
 * String authuserName = "support@cloudmicrofinance.com";
 * 
 * String authuser = "support@cloudmicrofinance.com"; String authpwd =
 * "support80";
 * 
 * // Very Important, Don't use email.setAuthentication()
 * email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
 * email.setDebug(false); // true if you want to debug
 * email.setHostName("smtp.gmail.com"); try {
 * email.getMailSession().getProperties().put("mail.smtp.starttls.enable",
 * "true"); email.setFrom(authuser, authuserName);
 * 
 * StringBuilder subjectBuilder = new
 * StringBuilder().append("MifosX Prototype Demo: "
 * ).append(emailDetail.getContactName()).append(" user account creation.");
 * 
 * email.setSubject(subjectBuilder.toString());
 * 
 * String sendToEmail = emailDetail.getAddress();
 * 
 * StringBuilder messageBuilder = new
 * StringBuilder().append("You are receiving this email as your email account: "
 * ).append(sendToEmail).append(
 * " has being used to create a user account for an organisation named ["
 * ).append
 * (emailDetail.getOrganisationName()).append("] on MifosX Prototype Demo."
 * ).append
 * ("You can login using the following credentials: username: ").append(emailDetail
 * .getUsername()).append(" password: ").append(unencodedPassword);
 * 
 * email.setMsg(messageBuilder.toString());
 * 
 * email.addTo(sendToEmail, emailDetail.getContactName()); email.send(); } catch
 * (EmailException e) { throw new PlatformEmailSendException(e); }
 */
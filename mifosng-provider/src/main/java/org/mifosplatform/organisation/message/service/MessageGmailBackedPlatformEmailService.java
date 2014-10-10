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
package org.mifosplatform.organisation.message.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.GlobalConfigurationPropertyNotFoundException;
import org.mifosplatform.organisation.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.MessageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageGmailBackedPlatformEmailService implements MessagePlatformEmailService {
	
	private final MessageDataRepository messageDataRepository;
	private final GlobalConfigurationRepository repository;
	private String authuser;
	private String encodedPassword;
	private String authpwd;
	private String hostName;
	private int portNumber;
	private GlobalConfigurationProperty configuration;
	
	@Autowired
	public MessageGmailBackedPlatformEmailService(MessageDataRepository messageDataRepository,final GlobalConfigurationRepository repository) {

		this.messageDataRepository = messageDataRepository;
		this.repository=repository;
		SmtpDataProcessing();
	}
	
	public void SmtpDataProcessing(){
		
		configuration=repository.findOneByName("SMTP");
		
		if(configuration != null){
			
			 String value= configuration.getValue();
		      
		        try {
					JSONObject object =new JSONObject(value);
					authuser=(String) object.get("mailId");
					encodedPassword=(String) object.get("password");
					authpwd=new String(Base64.decodeBase64(encodedPassword));
					hostName=(String) object.get("hostName");
					String port=object.getString("port");
					if(port.isEmpty()){
						portNumber=Integer.parseInt("25");
					}else{
						portNumber=Integer.parseInt(port);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
       
	}
	
	public String rechecking(){
		configuration = null;
		SmtpDataProcessing();
		if(configuration != null){
			return "success";
		}else{
			return "failure";
		}
	}

	@Override
	public String sendToUserEmail(BillingMessageDataForProcessing emailDetail) {
				
		if(configuration != null){
			
			 //1) get the session object      
		     Properties properties = System.getProperties();  
		     properties.setProperty("mail.smtp.host", hostName);   
		     properties.put("mail.smtp.auth", "true");    

		     Session session = Session.getDefaultInstance(properties,   
		             new javax.mail.Authenticator() {   
		         protected PasswordAuthentication getPasswordAuthentication() {   
		             return new PasswordAuthentication(authuser,authpwd);    }   });       

		     //2) compose message      
		     try{
		    	 Date date=new Date();
				 String dateTime=date.getHours()+""+date.getMinutes();
			     String fileName="Statement_"+new LocalDate().toString().replace("-","")+"_"+dateTime+".pdf";
		    	 
		    	 MimeMessage message = new MimeMessage(session);    
		         message.setFrom(new InternetAddress(authuser));     
		         message.addRecipient(Message.RecipientType.TO,new InternetAddress(emailDetail.getMessageTo()));    
		         message.setSubject(emailDetail.getSubject());     
		         
		         StringBuilder messageBuilder = new StringBuilder()
			     .append(emailDetail.getHeader()+'\n')   
			     .append(emailDetail.getBody()+'\n')
			     .append(emailDetail.getFooter());

		         //3) create MimeBodyPart object and set your message text        
		         BodyPart messageBodyPart1 = new MimeBodyPart();     
		         messageBodyPart1.setText(messageBuilder.toString());      
		       
		         //5) create Multipart object and add MimeBodyPart objects to this object        
		         Multipart multipart = new MimeMultipart();    
		         multipart.addBodyPart(messageBodyPart1);  
		         if(!emailDetail.getAttachment().isEmpty()){
		        	 //4) create new MimeBodyPart object and set DataHandler object to this object        
			         MimeBodyPart messageBodyPart2 = new MimeBodyPart();      
			         String filename = emailDetail.getAttachment();//change accordingly     
			         DataSource source = new FileDataSource(filename);    
			         messageBodyPart2.setDataHandler(new DataHandler(source));    
			         messageBodyPart2.setFileName(fileName); 
		        	 multipart.addBodyPart(messageBodyPart2);    
		         }
		           
		         //6) set the multiplart object to the message object    
		         message.setContent(multipart );        

		         //7) send message    
		         Transport.send(message);      
		         System.out.println("message sent....");   
				BillingMessage billingMessage = this.messageDataRepository.findOne(emailDetail.getId());
				if (billingMessage.getStatus().contentEquals("N")) {
					billingMessage.updateStatus();
				}
				this.messageDataRepository.save(billingMessage);
				return "success";
				
		     }catch(Exception e){
		    	 handleCodeDataIntegrityIssues(null, e);
			     return e.getMessage();
		     }
		        
		}else{
			String result = rechecking();
			if(result.equalsIgnoreCase("failure")){
				throw new GlobalConfigurationPropertyNotFoundException("SMTP GlobalConfiguration Property Not Found"); 
			}else{
				return sendToUserEmail(emailDetail);
			}
			
		}
		
            
		
	}

	private void handleCodeDataIntegrityIssues(Object object, Exception dve) {
		// TODO Autogenerated method stub

	}

	@Override
	public String sendToUserMobile(String message, Long id) {
		// TODO Autogenerated method stub
		try {
			
			String retval = "";
			URL url = new URL("http://smscountry.com/SMSCwebservice_Bulk.aspx");

			HttpURLConnection urlconnection = (HttpURLConnection) url
					.openConnection();

			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("ContentType",
					"application/xwwwformurlencoded");
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
			return "success";

		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException : "
					+ e.getMessage() + " . encoding pattern not supported.");
			return e.getMessage();
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException : " + e.getMessage()
					+ " . URL is not located.");
			return e.getMessage();
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage() + ".");
			return e.getMessage();
		}
	}

	@Override
	public String createEmail(String pdfFileName, String emailId) {
		
		if(configuration != null){
			
			Date date=new Date();
			String dateTime=date.getHours()+""+date.getMinutes();
		    String fileName="ReportEmail_"+new LocalDate().toString().replace("-","")+"_"+dateTime+".pdf";
		    Properties props = new Properties();
		    props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", hostName);
			props.put("mail.smtp.port", portNumber);

			Session session = Session.getInstance(props,new javax.mail.Authenticator() {
			      protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(authuser, authpwd);
			      }
			});

			try {

				Message message = new MimeMessage(session);
			    message.setFrom(new InternetAddress(emailId));
			    message.setRecipients(Message.RecipientType.TO,
			       InternetAddress.parse(emailId));
			    message.setSubject("ReportEmail");
					
				MimeBodyPart messageBodyPart = new MimeBodyPart();

			    Multipart multipart = new MimeMultipart();
			  
			    String file = pdfFileName;
			    DataSource source = new FileDataSource(file);
			    messageBodyPart.setDataHandler(new DataHandler(source));
			    messageBodyPart.setFileName(fileName);
			    multipart.addBodyPart(messageBodyPart);
			    message.setContent(multipart);
			    System.out.println("Sending");
				Transport.send(message);
			    System.out.println("Done");
			    return "Success";
		    } catch (MessagingException e) {
			      throw new RuntimeException(e);
			}
			
		}else{
			String result = rechecking();
			if(result.equalsIgnoreCase("failure")){
				throw new GlobalConfigurationPropertyNotFoundException("SMTP GlobalConfiguration Property Not Found"); 
			}else{
				return createEmail(pdfFileName,emailId);
			}
		}
		 
		    
	}

	@Override
	public String sendGeneralMessage(String emailId, String body ,String subject) {
		
		if(configuration != null){
		
			Email email = new SimpleEmail();
				// Very Important, Don't use email.setAuthentication()
			email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
			email.setDebug(false); // true if you want to debug
			email.setHostName(hostName);
		
			try {
				String sendToEmail = emailId;
				StringBuilder messageBuilder = new StringBuilder().append(body);			
				email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
				email.setFrom(authuser, authuser);
				email.setSmtpPort(portNumber);
				email.setSubject(subject);		
				email.addTo(sendToEmail, sendToEmail);
				email.setMsg(messageBuilder.toString());
				email.send();
				return "Success";
			} catch (Exception e) {
				handleCodeDataIntegrityIssues(null, e);
				return e.getMessage();
			}
			
		}else{
			String result = rechecking();
			if(result.equalsIgnoreCase("failure")){
				throw new GlobalConfigurationPropertyNotFoundException("SMTP GlobalConfiguration Property Not Found"); 
			}else{
				return sendGeneralMessage(emailId,body,subject);
			}
		}
		       
	}
}
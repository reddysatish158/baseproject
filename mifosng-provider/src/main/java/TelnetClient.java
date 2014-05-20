import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

class TelnetClient
{
    public static void main(String args[]) throws Exception
    {
    	Email email = new SimpleEmail();

        String authuserName = "kiran@hugotechnologies.com";//"billing@clear-tv.com";//"Open Billing System Community";

        String authuser = "kiran@hugotechnologies.com";//"billing@clear-tv.com";//"info@openbillingsystem.com";
        String authpwd ="kirankiran"; //"openbs@13";

        // Very Important, Don't use email.setAuthentication()
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        //email.setHostName("smtp.gmail.com");relay.jangosmtp.net
        email.setHostName("relay.jangosmtp.net");
        try {
            email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            email.setFrom(authuser, authuserName);
            System.out.println("Sending Email.....");
            StringBuilder subjectBuilder = new StringBuilder().append("BillingX Prototype Demo: ").append(" user account creation.");

            email.setSubject(subjectBuilder.toString());

            String sendToEmail = "kiran@hugotechnologies.com";

            StringBuilder messageBuilder = new StringBuilder().append("You are receiving this email as your email account: ").append(sendToEmail).append(" has being used to create a user account for an organisation named [").append("] on BillingX Prototype Demo.").append("You can login using the following credentials: username: ").append(" password: ").append("");

            email.setMsg(messageBuilder.toString());

            email.addTo(sendToEmail, "kiran");
            email.send();
            System.out.println("SentEmail.....");
        } catch (EmailException e) {
        	
        	System.out.println(e.getMessage());
            
        }
    }
    }
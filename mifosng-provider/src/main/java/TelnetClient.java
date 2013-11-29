import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Properties;

class TelnetClient
{
    public static void main(String args[]) throws Exception
    {
    	
    	try{
    		
    	Properties  prop=new Properties();
    	prop.load(new FileInputStream("TelNet.properties"));
    	
    	String srno = prop.getProperty("srno");
    	System.out.println(srno);
        Socket soc=new Socket("172.20.244.69",11010);
        
        DataInputStream din=new DataInputStream(soc.getInputStream());        
        DataOutputStream dout=new DataOutputStream(soc.getOutputStream());

        System.out.println("Welcome to Telnet Client");
         System.out.println("For Regitstration....");
         
         
         Integer serialno=new Integer(srno);
       
        dout.writeUTF(srno+",REGIST,000005");
        System.out.println("Registered Successfully...");
        
        
        	  
        	String response=din.readUTF();
        	if(response!=null){
        	System.out.println("Registered Successfully..."+response);
        	}
        	
        	  
        //For Activation
        	String Activation = prop.getProperty("Activation");
        	int activatioSrno=serialno++;
        System.out.println("waiting for Activating...");
        dout.writeUTF(activatioSrno+",IMSENT,NSC001,("+Activation+")");
        
        String ActResponse=din.readUTF();
    	if(ActResponse!=null){
    		System.out.println("Activated Successfully..."+ActResponse);
    	}
        /*//For Disconnection
        System.out.println("Disconnecting...");
        dout.writeUTF("352,IMSDEL,NSC001,(5510027283)");
       
        String diResponse=din.readUTF();
    	if(diResponse!=null){
    		 System.out.println("Disconnected Successfully..."+diResponse);
    	}*/
        
        dout.writeUTF("353,INDOSM,5510027283,3,ONCE,10,'Hurray OBS did it!'");
        
        String messResponse=din.readUTF();
    	if(messResponse!=null){
    		 System.out.println(messResponse);
    	}
        
         
        soc.close();        
    }catch(Exception exception){
    	
    	System.out.println("unable to connect");
    }
    }
}
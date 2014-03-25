import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class TelnetClient
{
    public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("172.30.9.18",23);
        String LoginName;
        String Password;
        String Command;
        
        
        DataInputStream din=new DataInputStream(soc.getInputStream());        
        
        DataOutputStream dout=new DataOutputStream(soc.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
/*
        System.out.println("Welcome to Telnet Client");
        System.out.println("Your Credential Please...");
        System.out.print("Login Name :");*/
/*
        LoginName=br.readLine();
        
        System.out.print("Password :");
        Password=br.readLine();*/
        
        if (din.readUTF().equals("password"))
        {           
             dout.writeUTF("obs@spice");
        
        }else{
        	 dout.writeUTF("obs@spice");
        }
        
       
        dout.writeUTF("class new inbound/testobs1 testone2 inside");

       
        soc.close();        
    }
}
 
import java.math.BigInteger;
import java.security.MessageDigest;
 
public class MD5HashingExample 
{
    public static void main(String[] args)throws Exception
    {
    	String password = "010604B800010018000100010000F0F06d6109cfa94ee9b1faccb0cb523a8221";
    	char session[] =
    		{0x01,0x06,0x00,0x01,0x00,0x01,0x00,0x18,
    		0x00,0x01,0x00,0x01,0x00,0x00,0xf0,0xf0,0xe1,0xee,0x84,0x14,0x74,
    		0xbb,0x8e,0x30,0xad,0xd7,0xe8,0xb1,0xbd,0x52,0x0b,0xd7};
    	 String string=String.format("%040x", new BigInteger(1, password.getBytes("UTF-8")));
    	 System.out.println(session);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        System.out.println("Digest(in hex format):: " + sb.toString());
 
        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<byteData.length;i++) {
    		String hex=Integer.toHexString(0xff & byteData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
   	     	
    	}
    	System.out.println("Digest(in hex format):: " + hexString.toString());
    }
}
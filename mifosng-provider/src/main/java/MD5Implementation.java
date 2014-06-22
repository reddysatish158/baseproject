import java.io.UnsupportedEncodingException;


public class MD5Implementation{
public static void main(String args[]) throws UnsupportedEncodingException{

	String message="010604B800010018000100010000F0F0E1EE841474BB8E30ADD7E8B1BD520BD7";
	char session[] =
		{0x01,0x06,0x00,0x01,0x00,0x01,0x00,0x18,
			0x00,0x01,0x00,0x01,0x00,0x00,0xf0,0xf0,0xe1,0xee,0x84,0x14,0x74,
			0xbb,0x8e,0x30,0xad,0xd7,0xe8,0xb1,0xbd,0x52,0x0b,0xd7};
	   byte[] byteMama = new byte[session.length];  
	   
       for(int i=0;i<session.length;i++){  
           byteMama[i]= (byte) session[i];  
       }  
	byte[] string=new String(session).getBytes("UTF-8");
	System.out.println("Input String : "+byteMama);
	byte buf[] = message.getBytes();
	System.out.println(string);
	MD5 md = new MD5();
	byte out[] = new byte[60];
	md.Update(string);
	MD5.asHex(out);
	String nhash = md.asHex();
	System.out.println("MD5 Output(Hashcode) : "+nhash);
}
}
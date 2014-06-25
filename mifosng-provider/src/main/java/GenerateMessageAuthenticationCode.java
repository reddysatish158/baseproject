import org.json.JSONArray;
import org.json.JSONException;




public class GenerateMessageAuthenticationCode {
	
	public static void main(String[] args) throws JSONException {
	
		
		StringBuilder s =new StringBuilder();
		JSONArray array=new JSONArray();
		array.put("192.168.1.2");
		array.put("192.168.1.3");
		System.out.println(array.toString());
		System.out.println(array.length());
		for(int i=0;i<array.length();i++){
			System.out.println(array.get(0));	
		}
		
		System.out.println(array.get(1));
	/*	String[] oldIpAddressArray = array.toString().split("(?!^)"); // new String[] {array.toString()};
		
		for(String o:oldIpAddressArray){
			//array.put(o);
			System.out.println(o);
		}*/
		
		
		
	}
	}

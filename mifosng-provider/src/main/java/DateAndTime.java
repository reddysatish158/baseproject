import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;





public class DateAndTime {
	
	public static void main(String[] args) {
		
		String string="{"+"billAlign"+":false"+",planCode"+":1}";
		JsonParser parser=new JsonParser();
		Object obj=parser.parse(string);
		JsonElement jsonObject=parser.parse(string);		
		
		String[] strings=string.split(" ");
		System.out.println(strings[1]);
		
	}

}


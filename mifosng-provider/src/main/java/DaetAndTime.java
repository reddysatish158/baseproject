import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


 
public class DaetAndTime {
     
     
   
	public static void main(String[] args) throws JSONException {/*
  JSONArray jsonArray=new JSONArray();
	JSONObject jsonObject=new JSONObject();
	JSONObject jsonObject1=new JSONObject();
	jsonObject.put("key1","k");
	jsonObject.put("key2","k1");
	
	jsonArray.put(jsonObject);
	jsonObject1.put("ser", jsonArray);
	System.out.println(jsonArray);
	System.out.println(jsonObject1);
	JSONArray array=jsonObject1.getJSONArray("ser");
	JSONArray array2=new JSONArray();
	JSONObject object=array.getJSONObject(0);
	object.put("key3", "k3");
	
	array2.put(object);
	System.out.println(array2);
	
	
	*/
	JsonArray jsonArray=new JsonArray();
	JsonArray jsonArray1=new JsonArray();
	JsonObject object=new JsonObject();
	object.addProperty("k1", "k");
	object.addProperty("k2", "k1");
	jsonArray.add(object);
	System.out.println(jsonArray);
	
	for(JsonElement jsonElement:jsonArray){
		JsonObject object2=jsonElement.getAsJsonObject();
		object2.addProperty("k3", "k2");
		jsonArray1.add(object2);
		
	}
	System.out.println(jsonArray1);
	}
	
	
	
}

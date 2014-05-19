import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Exampl {

	//rivate final PasswordEncoder encoder;
  
    public static void main(String[] args) throws ParseException, JSONException {

    	String string="{'name':'kiran'}";
				JSONObject jsonObject=new JSONObject(string);
				System.out.println(jsonObject.get("name"));
			}

		

    }


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Incident {
	String TomTomURL = "https://api.tomtom.com/traffic/services/4/incidentDetails/s2/" + //main url
				 "42.055593,-88.036812,41.622812,-87.444923/12/-1/xml?projection=EPSG4326&" + //location 
				 "key=8274p6y8umxn4tey9jrr6tqh"; //api key
	
	OkHttpClient client = new OkHttpClient();
	DBMS database = new DBMS();
	
	public void readInIncidents(){
		String sql = tomtom(getIncidents());
		database.query(sql);
	}
	
	private String getIncidents() {
		Request request = new Request.Builder().url(TomTomURL).build();
		
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
		catch(IOException ex){
			System.out.println("Failed to get incidents");
		}
		return "fail";
	}
	
	private String tomtom(String line){
		return "";
	}
}

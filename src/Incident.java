import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Incident {
	String TomTomURL = 
			"https://api.tomtom.com/traffic/services/4/incidentDetails/s2/" + //main url
			"42.055593,-88.036812,41.622812,-87.444923/12/-1/xml?projection=EPSG4326&" + //location 
			"key=8274p6y8umxn4tey9jrr6tqh"; //api key
	
	OkHttpClient client = new OkHttpClient();
	DBMS database = new DBMS();
	
	public void readInIncidents(){
		System.out.print("Reading Incidents...");
		long start_time = System.currentTimeMillis();
		
		String incidents = getIncidents();
		String sql = tomtomToSQL(incidents);
		database.query(sql);
		
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		return;
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
	
	private String tomtomToSQL(String incidents){
		String sql = "";
		int start = 0, end = 0;
		
		String id, lat, lon, category, from, to, length, delay;
		while((start = incidents.indexOf("<poi>", start)) != -1){
			start = incidents.indexOf("<id>", start) + "<id>".length();
			end = incidents.indexOf("</id>", start);
			id = incidents.substring(start, end);
			
			start = incidents.indexOf("<x>", start) + "<x>".length();
			end = incidents.indexOf("</x>", start);
			lat = incidents.substring(start, end);
			
			start = incidents.indexOf("<y>", start) + "<y>".length();
			end = incidents.indexOf("</y>", start);
			lon = incidents.substring(start, end);
			
			start = incidents.indexOf("<ic>", start) + "<ic>".length();
			end = incidents.indexOf("</ic>", start);
			category = incidents.substring(start, end);
			
			start = incidents.indexOf("<f>", start) + "<f>".length();
			end = incidents.indexOf("</f>", start);
			from = incidents.substring(start, end);
			
			start = incidents.indexOf("<t>", start) + "<t>".length();
			end = incidents.indexOf("</t>", start);
			to = incidents.substring(start, end);
			
			start = incidents.indexOf("<l>", start) + "<l>".length();
			end = incidents.indexOf("</l>", start);
			length = incidents.substring(start, end);
			
			start = incidents.indexOf("<dl>", start) + "<dl>".length();
			end = incidents.indexOf("</dl>", start);
			delay = incidents.substring(start, end);
			
			
			sql +=   "INSERT INTO "+database.incTable+" VALUES ("+
		    	     "'"+id+"', "+
		    	     lat+", "+
		    	     lon+", "+
		    	     category+", "+
		    	     "'"+from+"', "+
		    	     "'"+to+"', "+
		    	     length+", "+
		    	     delay+"); ";;
		}
		return sql;
	}
}

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Incident {
	static String TomTomURL = "https://api.tomtom.com/traffic/services/4/incidentDetails/s1/42.055593,-88.036812,41.622812,-87.444923/12/-1/xml?projection=EPSG4326&key=8274p6y8umxn4tey9jrr6tqh";

	OkHttpClient client = new OkHttpClient();
	DBMS database = new DBMS();
	
	public void readInIncidents(){
		System.out.print("Reading Incidents...");
		long start_time = System.currentTimeMillis();
		
		String incidents = getIncidents(); //returns a string containing the incidents from the api
		String sql = "TRUNCATE TABLE "+database.getIncTable()+"; "; 
		sql += tomtomToSQL(incidents); //converts the string to sql code
		database.updateQuery(sql); //queries the sql code to the database
		
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
		
		String inc;
		while((start = incidents.indexOf("<poi>", start)) != -1){
			end = incidents.indexOf("</poi>", start);
			inc = incidents.substring(start, end);
			
			sql += IncidentToSQL(inc);
			start = end;
		}
		return sql;
	}
	
	private String IncidentToSQL(String inc){
		int start = 0, end = 0;
		String id, lat, lon, category, from, to, length, delay;
		
		//id
		if((start = inc.indexOf("<id>", start)) != -1){
			end = inc.indexOf("</id>", start);
			id = inc.substring(start, end);
		}
		else{
			return "";
		}
		
		//lat
		if((start = inc.indexOf("<x>", start) + "<x>".length()) != -1 + "<x>".length()){
			end = inc.indexOf("</x>", start);
			lat = inc.substring(start, end);
		}
		else{
			return "";
		}
		
		//lon
		if((start = inc.indexOf("<y>", start) + "<y>".length()) != -1 + "<y>".length()){
			end = inc.indexOf("</y>", start);
			lon = inc.substring(start, end);
		}
		else{
			return "";
		}
		
		//category
		if((start = inc.indexOf("<ic>", start) + "<ic>".length()) != -1 + "<ic>".length()){
			end = inc.indexOf("</ic>", start);
			category = inc.substring(start, end);
		}
		else{
			category = "-1";
		}
		
		//from
		if((start = inc.indexOf("<f>", start) + "<f>".length()) != -1 + "<f>".length()){
			end = inc.indexOf("</f>", start);
			from = inc.substring(start, end);
		}
		else {
			from = "none";
		}
		
		//to
		if((start = inc.indexOf("<t>", start) + "<t>".length()) != -1 + "<t>".length()){
			end = inc.indexOf("</t>", start);
			to = inc.substring(start, end);
		}
		else{
			to = "none";
		}
		
		//length
		if((start = inc.indexOf("<l>", start) + "<l>".length()) != -1 + "<l>".length()){
			end = inc.indexOf("</l>", start);
			length = inc.substring(start, end);
		}
		else{
			length = "-1";
		}
		
		//delay
		if((start = inc.indexOf("<dl>", start) + "<dl>".length()) != -1 + "<dl>".length()){
			end = inc.indexOf("</dl>");
			delay = inc.substring(start, end);
		}
		else{
			delay = "-1";
		}
		
		
		return   "INSERT INTO "+database.getIncTable()+" VALUES ("+
	    	     "\""+id+"\", "+
	    	     lat+", "+
	    	     lon+", "+
	    	     category+", "+
	    	     "\""+from+"\", "+
	    	     "\""+to+"\", "+
	    	     length+", "+
	    	     delay+"); ";
	}
}

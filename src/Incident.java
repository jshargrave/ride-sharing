import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Incident {
	String TomTomKeyIncident = "8274p6y8umxn4tey9jrr6tqh";
	String TomTomKeyFlow = "ZBrRIWNQ3IoaQw7hZa376735FBAmKYSl";
	String TomTomKeySearch = "aks4juru9smr88vny9z6qkw9";
	
	String coords[] = {"42.055593", "-88.036812", "41.622812", "-87.444923"};
	String coordsString = coords[0]+","+coords[1]+","+coords[2]+","+coords[3];

	String TomTomIncidentURL = "https://api.tomtom.com/traffic/services/4/incidentDetails/s1/"+coordsString+"/12/-1/xml?projection=EPSG4326&key="+TomTomKeyIncident;
	String TomTomAddressURL = "";
	
	OkHttpClient client = new OkHttpClient();
	DBMS database = new DBMS();
	
	//pull incident data from api and loads it into database
	public void readInIncidents(){
		System.out.print("Reading Incidents...");
		long start_time = System.currentTimeMillis();
		
		String incidents = queryAPI(TomTomIncidentURL); //returns a string containing the incidents from the api
		String sql = tomtomToSQL(incidents); //converts the string to sql code
		database.updateQuery(sql); //queries the sql code to the database
		
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		return;
	}
	
	//Provided the url to then query and API and then return the results in a string
	private String queryAPI(String S) {
		Request request = new Request.Builder().url(S).build();
		
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
		catch(IOException ex){
			System.out.println("API query failed...");
		}
		return "fail";
	}
	
	//converts the tomtom incident api results to a sql format to be inserted into database
	private String tomtomToSQL(String incidents){
		String sql = "TRUNCATE TABLE "+database.getIncTable()+"; "+
				     "INSERT INTO "+database.getIncTable()+"(inc, lat, lon, category, from_road, to_road, distance_delay, delay_time) VALUES ";
		int start = 0, end = 0;
		
		StringBuilder sb = new StringBuilder();
		String inc;
		while((start = incidents.indexOf("<poi>", start)) != -1){
			end = incidents.indexOf("</poi>", start);
			inc = incidents.substring(start, end);
			
			sb.append(String.format("%s", IncidentToSQL(inc)));
			start = end;
		}
		sql += sb.toString();

		return sql.substring(0, sql.length() - 1) + ";";
	}
	
	//converts a single tomtom incident to a sql format to be inserted into the database
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
			lon = inc.substring(start, end);
		}
		else{
			return "";
		}
		
		//lon
		if((start = inc.indexOf("<y>", start) + "<y>".length()) != -1 + "<y>".length()){
			end = inc.indexOf("</y>", start);
			lat = inc.substring(start, end);
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
			from = parseAddress(from);
		}
		else {
			from = "none";
		}
		
		//to
		if((start = inc.indexOf("<t>", start) + "<t>".length()) != -1 + "<t>".length()){
			end = inc.indexOf("</t>", start);
			to = inc.substring(start, end);
			to = parseAddress(to);
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
		
		//note: strings should be surrounded by double quotes because street names can contain single quotes
		return   "(\""+id+"\","+lat+","+lon+","+category+",\""+from+"\","+"\""+to+"\","+length+","+delay+"),";
	}
	
	//parses the address recieved from the incident api so that it can be used with the search api
	private String parseAddress(String S){
		return S.replace("/", ",").replace(" (", "&").replace(")", "").replace(" ", "_");
	}
	
	//returns a set of lat/lon coords from the search api of the address passed to the function
	public double[] getAddressCoords(String S){
		String url = "https://api.tomtom.com/search/2/search/"+S+".xml?key="+TomTomKeySearch+"&countrySet=US?topleft="+coords[0]+","+coords[1]+"?btmRight="+coords[2]+","+coords[3]+"&idxSet=Xstr";
		String search = queryAPI(url);
		
		int start = 0, end = 0;
		
		start = search.indexOf("<lat>", start) + "<lat>".length();
		end = search.indexOf("</lat>", start);
		double lat = Double.parseDouble(search.substring(start, end));
		
		start = search.indexOf("<lon>", start) + "<lon>".length();
		end = search.indexOf("</lon>", start);
		double lon = Double.parseDouble(search.substring(start, end));
		
		double results[] = {lat, lon};
		return results;
	}
	
	//returns a float array of details pertaining to a specific segment, the segment is matched to the closest one using the coordinates passed
	public float[] getSegmentData(String coord1, String coord2){
		String url = "https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/10/xml?key="+TomTomKeyFlow+"&point="+coord1+","+coord2;
		String flow = queryAPI(url);
		
		int start = 0, end = 0;
		
		start = flow.indexOf("<currentSpeed>", start) + "<currentSpeed>".length();
		end = flow.indexOf("</currentSpeed>", start);
		int currentSpeed = Integer.parseInt(flow.substring(start, end));
		
		start = flow.indexOf("<freeFlowSpeed>", start) + "<freeFlowSpeed>".length();
		end = flow.indexOf("</freeFlowSpeed>", start);
		int freeFlowSpeed = Integer.parseInt(flow.substring(start, end));

		start = flow.indexOf("<currentTravelTime>", start) + "<currentTravelTime>".length();
		end = flow.indexOf("</currentTravelTime>", start);
		int currentTravelTime = Integer.parseInt(flow.substring(start, end));
		
		start = flow.indexOf("<freeFlowTravelTime>", start) + "<freeFlowTravelTime>".length();
		end = flow.indexOf("</freeFlowTravelTime>", start);
		int freeFlowTravelTime = Integer.parseInt(flow.substring(start, end));
		
		start = flow.indexOf("<confidence>", start) + "<confidence>".length();
		end = flow.indexOf("</confidence>", start);
		float confidence = Float.parseFloat(flow.substring(start, end));
		
		float results[] = {currentSpeed, freeFlowSpeed, currentTravelTime, freeFlowTravelTime, confidence};
		return results;
	}
}

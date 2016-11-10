

public class Incident {
	String url = "https://api.tomtom.com/traffic/services/4/incidentDetails/s2/" + //main url
				 "42.055593,-88.036812,41.622812,-87.444923/12/-1/xml?projection=EPSG4326&" + //location 
				 "key=8274p6y8umxn4tey9jrr6tqh"; //api key
	
	String insertTable = "";
	DBMS database = new DBMS();
	
	
	public void getIncidents(){
		
	}
}

import java.io.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class GPS_Data {
	static String GPSFile = "files/SanFransisco.txt"; //file to read in
	int TimeInc = 60; //time increment to partition road networks by in minutes, the increment should be a multiple of 24*60
	
	DBMS database = new DBMS(); //used to read in road network to database
	
	public void readInGPS(){
		String line = null;
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader GPSIn = new FileReader(GPSFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(GPSIn);
            
            System.out.print("Reading in GPS data... ");
            long start_time = System.currentTimeMillis();
            
            String sql = "TRUNCATE TABLE "+database.getGPSTable()+"; "+
       		     		 "INSERT INTO "+database.getGPSTable()+" (car_id, log_time, lat, lon) "+
       		     		 "VALUES ";
            
            StringBuilder sb = new StringBuilder();
            
            while((line = bufferedReader.readLine()) != null) {
            	sb.append(String.format("%s", SanFransiscoGPS(line)));
            }
            sql += sb.toString();
            sql = sql.substring(0, sql.length() - 1) + ";";
            
            database.updateQuery(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");

            // Always close files.
            bufferedReader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + GPSFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + GPSFile + " ...");
		}
		return;
	}
	
	//the read in method for a sanFransisco GPS dataset
	public String SanFransiscoGPS(String line){
		String id, time, lat, lon;
		int start, end;
		
		start = 0;
        end = line.indexOf("	", start + 1);
    	id = line.substring(start, end);
    	
    	start = line.indexOf("T") + 1;
    	end = line.indexOf("+", start + 1);
    	time = line.substring(start, end);
    	
        start = line.indexOf("	", end) + 1;
        end = line.indexOf("	", start + 1);
        lat = line.substring(start, end);
        
        start = end + 1;
        end = line.length();
        lon = line.substring(start, end);
         
        return "("+id+", '"+time+"', "+lat+", "+lon+"),";
	}
	
	//Estimates the speed between gps points in the database
	public void estimateSpeed(){
		String sql = "SELECT * FROM "+database.getGPSTable();
		List<Map<String, Object>> results = database.exicuteQuery(sql);
		
		System.out.print("Estimating GPS speed... ");
		long start_time = System.currentTimeMillis();
		
		boolean newID = true;
		
		int gps_id, car_id1, car_id2 = -1;
		int log_time1, log_time2 = 0;
		double lat1 = 0, lon1 = 0, lat2 = 0, lon2 = 0, dist, speed;
		sql = "";
		
		for(int i = 0; i < results.size(); i++){
			car_id1 = car_id2;
			car_id2 = Integer.parseInt(results.get(i).get("car_id").toString());
			
			if(car_id1 != car_id2){
				newID = true;
			}
			
			if(newID){
				lat1 = Double.parseDouble(results.get(i).get("lat").toString());
				lon1 = Double.parseDouble(results.get(i).get("lon").toString());
				log_time1 = convertTime(results.get(i).get("log_time").toString());
				i++;
			}
			else{
				lat1 = lat2;
				lon1 = lon2;
				log_time1 = log_time2;
			}
			
			gps_id = Integer.parseInt(results.get(i).get("gps_id").toString());
			lat2 = Double.parseDouble(results.get(i).get("lat").toString());
			lon2 = Double.parseDouble(results.get(i).get("lon").toString());
			log_time2 = convertTime(results.get(i).get("log_time").toString());
			
			dist = database.distance(lat1, lon1, lat2, lon2, "K");
			speed = dist/(log_time2 - log_time1) * 60 * 60;
			
			sql += "UPDATE "+database.getGPSTable()+" SET speed='"+speed+"' WHERE gps_id="+gps_id+"; ";
			if(newID){
				sql += "UPDATE "+database.getGPSTable()+" SET speed='"+speed+"' WHERE gps_id="+(gps_id-1)+"; ";
				newID = false;
			}
			
		}		
		database.updateQuery(sql);
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins; found ");
	}
	
	//passed a time variable HH:MM:SS and then converts it to a integer value
	private int convertTime(String s){
		String[] timef=s.split(":");  

		int hour=Integer.parseInt(timef[0]);  
		int minute=Integer.parseInt(timef[1]);  
		int second=Integer.parseInt(timef[2]);  

		return second + (60 * minute) + (3600 * hour);
	}
	
	//matches all the gps logs to segments and indexes
	public void matchLogToSeg(){
		System.out.print("Matching GPS to segments... ");
		long start_time = System.currentTimeMillis();
		
		List<Map<String, Object>> GPSResults = database.exicuteQuery("SELECT * FROM "+database.getGPSTable());
		List<Map<String, Object>> indexResults = database.exicuteQuery("SELECT * FROM "+database.getRNIndexTable());
		List<Map<String, Object>> partitionResults;
		
		String sql, index, arg1;
		int gps_id;
		double maxlat, maxlon, minlat, minlon, lat, lon, lat1, lon1, lat2, lon2, segID, totalDist, minDist;
		
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < GPSResults.size(); j++){
			
			lat = Double.parseDouble(GPSResults.get(j).get("lat").toString());
			lon = Double.parseDouble(GPSResults.get(j).get("lon").toString());
		
			for(int i = 0; i < indexResults.size(); i++){
				
				maxlat = Double.parseDouble(indexResults.get(i).get("max_lat").toString());
				maxlon = Double.parseDouble(indexResults.get(i).get("max_lon").toString());
				minlat = Double.parseDouble(indexResults.get(i).get("min_lat").toString());
				minlon = Double.parseDouble(indexResults.get(i).get("min_lon").toString());
				
				if(database.coordsInBox(maxlat, maxlon, minlat, minlon, lat, lon)){
					index = indexResults.get(i).get("table_id").toString();
					gps_id = Integer.parseInt(GPSResults.get(j).get("gps_id").toString());
					
					
					partitionResults = database.exicuteQuery("SELECT * FROM "+index);
					
					minDist = 1;
					segID = -1;
					for(int k = 0; k < partitionResults.size(); k++){
						lat1 = Double.parseDouble(partitionResults.get(k).get("lat1").toString());
						lon1 = Double.parseDouble(partitionResults.get(k).get("lon1").toString());
						lat2 = Double.parseDouble(partitionResults.get(k).get("lat2").toString());
						lon2 = Double.parseDouble(partitionResults.get(k).get("lon2").toString());
						
						totalDist = database.distance(lat1, lon1, lat, lon, "k") + database.distance(lat2, lon2, lat, lon, "k");
						
						
						if(totalDist < minDist){
							minDist = totalDist;
							segID = Double.parseDouble(partitionResults.get(k).get("seg_id").toString());
						}
					}
					
					arg1 = "UPDATE "+database.getGPSTable()+" SET index_id='"+index+"', seg_id='"+segID+"' WHERE gps_id="+gps_id+"; ";
					sb.append(String.format("%s", arg1));
					break;
					
				}
			}
		}
		sql = sb.toString();
		database.updateQuery(sql);
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
	
	//matches the average speed of segments with data to correct time partitions
	public void AvgSpeedToSeg(){
		System.out.print("Matching avg speed to segments... ");
		long start_time = System.currentTimeMillis();
		
		List<Map<String, Object>> resultsIndexs = database.exicuteQuery("SELECT table_id FROM "+database.getRNIndexTable());
		
		StringBuilder sb1 = new StringBuilder();
		String arg1, tableNameTime, tableName;
		
		LocalTime time;
		for(int j = 0; j < resultsIndexs.size(); j++){
			tableName = resultsIndexs.get(j).get("table_id").toString();
			
			time = LocalTime.of(0, 0, 0);
			for(int i = 1; i <= (60/TimeInc) * 24; i++){
				tableNameTime = tableName + "T"+time.toString().replaceAll(":", "_");
				
				arg1 = "INSERT INTO "+tableNameTime+" "+
					   "SELECT G.seg_id, AVG(speed) "+
					   "FROM tmp_gps AS G "+
					   "WHERE G.index_id='"+tableName+"' AND log_time BETWEEN '"+time.toString()+"' AND '"+time.plusMinutes(TimeInc).toString()+"' "+
					   "GROUP BY G.seg_id; ";
				
				sb1.append(String.format("%s", arg1));
				time = time.plusMinutes(TimeInc);
			}
		}
		database.updateQuery(sb1.toString());
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
}



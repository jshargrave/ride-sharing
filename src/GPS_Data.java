import java.io.*;
import java.util.List;
import java.util.Map;

public class GPS_Data {
	static String GPSFile = "files/SanFransisco.txt"; //file to read in
	
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
            
            
            while((line = bufferedReader.readLine()) != null) {
                sql += SanFransiscoGPS(line); //using the SanFransisco method
            }
            sql = sql.substring(0, sql.length() - 1) + ";";
            
            database.updateQuery(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("Completed: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");

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
	
	public void estimateSpeed(){
		String sql = "SELECT * FROM "+database.getGPSTable();
		
		List<Map<String, Object>> results = database.exicuteQuery(sql);
		
		System.out.print("Estimating GPS logs speed... ");
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
				log_time1 = convertToInt(results.get(i).get("log_time").toString());
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
			log_time2 = convertToInt(results.get(i).get("log_time").toString());
			
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
        System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins; found ");
	}
	
	private int convertToInt(String s){
		String[] timef=s.split(":");  

		int hour=Integer.parseInt(timef[0]);  
		int minute=Integer.parseInt(timef[1]);  
		int second=Integer.parseInt(timef[2]);  

		return second + (60 * minute) + (3600 * hour);
	}
}



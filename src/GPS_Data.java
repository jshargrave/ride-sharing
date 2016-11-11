import java.io.*;

public class GPS_Data {
	String GPSFile = "files/SanFransisco.txt"; //file to read in
	
	String insertTable = "";
	DBMS database = new DBMS(); //used to read in road network to database

	public GPS_Data(){
		readInGPS();
	}
	
	public void readInGPS(){
		
		
		String line = null;
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader GPSIn = new FileReader(GPSFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(GPSIn);
            
            System.out.print("Reading in GPS data... ");
            long start_time = System.currentTimeMillis();
            while((line = bufferedReader.readLine()) != null) {
                database.query(SanFransiscoGPS(line)); //using the SanFransisco method
            }
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
    	
    	start = line.indexOf("T", start) + 1;
    	end = line.indexOf("+", start + 1);
    	time = line.substring(start, end);
        
        start = line.indexOf("	", end);
        end = line.indexOf("	", start + 1);
        lat = line.substring(start, end);
        
        start = end + 1;
        end = line.length();
        lon = line.substring(start, end);
        
        int TIME = convertToTime(time);
        
        return "INSERT INTO table_name (id, time, lat, lon) " +
        			"VALUES ("+id+", "+TIME+", "+lat+", "+lon+")";
	}
	
	
	//takes a string time of the format HH:MM:SS and converts it to seconds
	public int convertToTime(String time){
		String ht, mt, st;
		int HT, MT, ST;
		
		int start, end;
		
		start = 0;
		end = time.indexOf(":", start);
		ht = time.substring(start, end);
		
		start = end + 1;
		end = time.indexOf(":", start);
		mt = time.substring(start, end);
		
		start = end + 1;
		end = time.length();
		st = time.substring(start, end);
		
		HT = Integer.parseInt(ht);
		MT = Integer.parseInt(mt);
		ST = Integer.parseInt(st);
		
		return HT * 60 * 60 + MT * 60 + ST;
	}
	
	/*
	private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}
		else if(unit == "F"){
			dist = dist * 5280;
		}

		return (dist);
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	*/
}



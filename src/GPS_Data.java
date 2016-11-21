import java.io.*;

public class GPS_Data {
	static String GPSFile = "files/SanFransisco.txt"; //file to read in
	
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
                database.updateQuery(SanFransiscoGPS(line)); //using the SanFransisco method
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
	
	*/
}



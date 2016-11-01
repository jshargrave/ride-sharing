import java.util.*;
import java.io.*;

public class GPS_Data {
	String GPSFile = "files/SanFransisco.txt";
	
	LinkedList<Data> D = new LinkedList<Data>();

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
            
            while((line = bufferedReader.readLine()) != null) {
                D.add(SanFransiscoGPS(line)); //using the SanFransisco method
            }   

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
	public Data SanFransiscoGPS(String line){
		long ID; 
		int TIME;
		double LAT, LON;
		
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
        
        ID = Long.parseLong(id);
        TIME = convertToTime(time);
        LAT = Double.parseDouble(lat);
        LON = Double.parseDouble(lon);
        
        return new Data(ID, TIME, LAT, LON);
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
	
	public void printGPS(){
		long size = D.size();
		Data tmp;
		
		System.out.println("Numer of Edges: " + size);
		for(int i = 0; i < size; i++){
			tmp = D.get(i);
			System.out.println(tmp.id + ", " + tmp.time + ", " + tmp.lat + ", " + tmp.lon);
		}
		return;
	}
}



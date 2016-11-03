import java.io.*;
import java.util.*;

public class Road_Network {	
	String NodeFile = "files/Nodes.txt";
	String EdgeFile = "files/Edges.txt";
	String SegmentFile = "files/Segment.txt";
	int MAX_DIS =  50; //ft away from node to be matched to it
	
	LinkedList<Segment> S = new LinkedList<Segment>();

	public Road_Network(){ //build road network
		ReadInSegment();
	}
	
	public void ReadInSegment(){
		String N_id, E_id, E_node1, E_node2, lat, lon;
		double LAT1 = 0, LAT2 = 0, LON1 = 0, LON2 = 0;
		
		try{
			
			// FileReader reads text files in the default encoding.
            
            FileReader EdgeIn = new FileReader(EdgeFile);

            // Always wrap FileReader in BufferedReader.
            
            BufferedReader EdgeReader = new BufferedReader(EdgeIn);
            
            String EdgeLine = null;
            String NodeLine = null;
            
            int start, end;
                    
            System.out.print("Reading in segments... ");
            
            long start_time = System.currentTimeMillis();
            while((EdgeLine = EdgeReader.readLine()) != null){
            	
            	start = 0;
            	end = EdgeLine.indexOf(",", start);
            	E_id = EdgeLine.substring(start, end);
            	
            	start = end + 1;
            	end = EdgeLine.indexOf(",", start);
            	E_node1 = EdgeLine.substring(start, end);
            	
            	start = end + 1;
            	end = EdgeLine.indexOf(",", start);
            	E_node2 = EdgeLine.substring(start, end);
            	
            	FileReader NodeIn = new FileReader(NodeFile);
            	BufferedReader NodeReader = new BufferedReader(NodeIn);
            	while((NodeLine = NodeReader.readLine()) != null){
              		start = 0;
            		end = NodeLine.indexOf(",", start);
            		N_id = NodeLine.substring(start, end);
            		  		
            		if(N_id.equals(E_node1)){
            			start = end + 1;
            			end = NodeLine.indexOf(",", start);
            			lat = NodeLine.substring(start, end);
            			
            			start = end + 1;
            			end = NodeLine.length();
            			lon = NodeLine.substring(start, end);
            			
            			LAT1 = Double.parseDouble(lat);
            			LON1 = Double.parseDouble(lat);
            		}
            		else if(N_id.equals(E_node2)){
            			start = end + 1;
            			end = NodeLine.indexOf(",", start);
            			lat = NodeLine.substring(start, end);
            			
            			start = end + 1;
            			end = NodeLine.length();
            			lon = NodeLine.substring(start, end);
            			
            			LAT2 = Double.parseDouble(lat);
            			LON2 = Double.parseDouble(lat);
            		}
            	}
            	
            	S.add(new Segment(E_id, E_node1, E_node2, LAT1, LAT2, LON1, LON2));
            	NodeReader.close();
            }
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("Completed: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
                    
            EdgeReader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + NodeFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + NodeFile + " ...");
		}
		return;
	}
	
	public void segmentMatch(Data d){
		//gps data d is given
		//LinkedList of Segment S is given
		
		int matches = 0;
		for(int i = 0; i < S.size(); i++){
			if(distance(S.get(i).lat1, S.get(i).lon1, d.lat, d.lon, "F") < MAX_DIS){
				d.edgeID = S.get(i).id;
				matches++;
			}
			if(distance(S.get(i).lat2, S.get(i).lon2, d.lat, d.lon, "F") < MAX_DIS){
				d.edgeID = S.get(i).id;
				matches++;
			}
			if(matches > 1){
				//purge data because to is matching with multiple nodes
			}
		}
		
	}
	
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

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	public void printSegments(){
		for(int i = 0; i < S.size(); i++){
			System.out.println(S.get(i).id);
		}
		return;
	}	
}

import java.io.*;
import java.util.*;

public class Road_Network {	
	String NodeFile = "files/Nodes.txt";
	String EdgeFile = "files/Edges.txt";
	
	LinkedList<Node> N = new LinkedList<Node>();
	LinkedList<Edge> E = new LinkedList<Edge>();

	public Road_Network(){ //build road network
		ReadInNodes();
		ReadInEdges();
	}
	
	public void ReadInNodes(){
		String id, lat, lon;
		
		long ID;
		double LAT, LON;
		
		String line = null;
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader NodeIn = new FileReader(NodeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(NodeIn);
            
            int start, end;
            while((line = bufferedReader.readLine()) != null) {
                start = 0;
                end = line.indexOf(",");
            	id = line.substring(start, end);
            	
            	start = end + 1;
            	end = line.indexOf(",", start);
                lat = line.substring(start, end);
                
                start = end + 1;
                end = line.length();
                lon = line.substring(start, end);
                
                ID = Long.parseLong(id);
                LAT = Double.parseDouble(lat);
                LON = Double.parseDouble(lon);
                
                //System.out.println(ID + ", " + LAT + ", " + LON);
                
                N.add(new Node(ID, LAT, LON));
            }   

            // Always close files.
            bufferedReader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + NodeFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + NodeFile + " ...");
		}
		return;
	}
	
	public void ReadInEdges(){
		String id;
		String node1;
		String node2;
		
		long ID, NODE1, NODE2;
		
		String line = null;
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader EdgeIn = new FileReader(EdgeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(EdgeIn);
            
            int start, end;
            while((line = bufferedReader.readLine()) != null) {
                start = 0;
                end = line.indexOf(",");
            	id = line.substring(start, end);
            	
            	start = end + 1;
            	end = line.indexOf(",", start);
                node1 = line.substring(start, end);
                
                start = end + 1;
                end = line.indexOf(",", start);
                node2 = line.substring(start, end);
                
                ID = Long.parseLong(id);
                NODE1 = Long.parseLong(node1);
                NODE2 = Long.parseLong(node2);
                
                //System.out.println(ID + ", " + NODE1 + ", " + NODE2);
                
                E.add(new Edge(ID, NODE1, NODE2));
            }   

            // Always close files.
            bufferedReader.close();  
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + EdgeFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + EdgeFile + " ...");
		}
		return;
	}
	
	public void printNodes(){
		long size = N.size();
		Node tmp;
		
		System.out.println("Numer of Nodes: " + size);
		for(int i = 0; i < size; i++){
			tmp = N.get(i);
			System.out.println(tmp.id + ", " + tmp.lat + ", " + tmp.lon);
		}
		return;
	}
	
	public void printEdges(){
		long size = E.size();
		Edge tmp;
		
		System.out.println("Numer of Edges: " + size);
		for(int i = 0; i < size; i++){
			tmp = E.get(i);
			System.out.println(tmp.id + ", " + tmp.node1 + ", " + tmp.node2);
		}
		return;
	}
}

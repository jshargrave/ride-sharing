import java.io.*;

public class Road_Network {	
	String NodeFile = "files/Nodes.txt";
	String EdgeFile = "files/Edges.txt";
	String SegmentFile = "files/Segment.txt";
	int MAX_DIS =  50; //ft away from node to be matched to it
	
	DBMS database = new DBMS(); //used to read in road network to database
	
	public void ReadInEdges(){
		String EdgeLine;
		
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader EdgeIn = new FileReader(EdgeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader EdgeReader = new BufferedReader(EdgeIn);
             
            System.out.print("Reading in segments... ");
            
            long start_time = System.currentTimeMillis();
            String sql = "";
            while((EdgeLine = EdgeReader.readLine()) != null){
            	sql += MNTG_Edge(EdgeLine);
            }
            
            database.query(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
                    
            EdgeReader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + EdgeFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + EdgeFile + " ...");
		}
		return;
	}
	
	public void ReadInNodes(){
		String NodeLine;
		
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader NodeIn = new FileReader(NodeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader NodeReader = new BufferedReader(NodeIn);
             
            System.out.print("Reading in nodes... ");
            
            long start_time = System.currentTimeMillis();
            String sql = "";
            while((NodeLine = NodeReader.readLine()) != null){
            	sql += MNTG_Node(NodeLine);
            }
            
            database.query(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
                    
            NodeReader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + NodeFile + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + NodeFile + " ...");
		}
		return;
	}
	
	private String MNTG_Edge(String EdgeLine){
		int start = 0;
    	int end = EdgeLine.indexOf(",", start);
    	String E_id = EdgeLine.substring(start, end);
    	
    	start = end + 1;
    	end = EdgeLine.indexOf(",", start);
    	String E_node1 = EdgeLine.substring(start, end);
    	
    	start = end + 1;
    	end = EdgeLine.indexOf(",", start);
    	String E_node2 = EdgeLine.substring(start, end);
		
    	return "INSERT INTO "+database.segTable+" VALUES ("+
    	       E_id+", "+
    	       E_node1+", "+
    	       E_node2+"); ";
	}
	
	private String MNTG_Node(String NodeLine){
		int start = 0;
    	int end = NodeLine.indexOf(",", start);
    	String N_id = NodeLine.substring(start, end);
    	
    	start = end + 1;
    	end = NodeLine.indexOf(",", start);
    	String lat = NodeLine.substring(start, end);
    	
    	start = end + 1;
    	end = NodeLine.length();
    	String lon = NodeLine.substring(start, end);
		
    	return "INSERT INTO "+database.nodeTable+" VALUES ("+
    	       N_id+", "+
    	       lat+", "+
    	       lon+"); ";
	}
}

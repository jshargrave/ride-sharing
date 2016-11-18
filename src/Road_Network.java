import java.io.*;


import java.sql.ResultSet;
import java.sql.SQLException;

public class Road_Network {	
	static String NodeFile = "files/Nodes.txt";
	static String EdgeFile = "files/Edges.txt";
	
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
            String sql = "TRUNCATE TABLE "+database.getEdgeTable()+"; ";
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
            String sql = "TRUNCATE TABLE "+database.getNodeTable()+"; ";
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
		
    	return "INSERT INTO "+database.getEdgeTable()+" VALUES ("+
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
		
    	return "INSERT INTO "+database.getNodeTable()+" VALUES ("+
    	       N_id+", "+
    	       lat+", "+
    	       lon+"); ";
	}
	
	public void partitionRN(){
		String sql = "SELECT node_id FROM "+database.getNodeTable();
		ResultSet rs = database.exicuteQuery(sql);
		int id;
		
		try{
			while(rs.next()){
				id = rs.getInt("node_id");
				System.out.println("here");
			}
		}
		catch (SQLException se){
			
		}
	}
}

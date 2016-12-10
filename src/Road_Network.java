import java.io.*;
import java.util.*;

public class Road_Network {	
	static String NodeFile = "files/Nodes.txt";
	static String EdgeFile = "files/Edges.txt";
	
	int partitionSize = 1; //measured in kilometers
	
	DBMS database = new DBMS(); //used to read in road network to database
	
	
	Road_Network(){
		//ReadInEdges();
		//ReadInNodes();
		//mergeNodeEdge();
	}
	public void ReadInEdges(){		
		String line = null;
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader EdgeIn = new FileReader(EdgeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader EdgeReader = new BufferedReader(EdgeIn);
             
            System.out.print("Reading in segments... ");
            
            long start_time = System.currentTimeMillis();
            
            
            String sql = "TRUNCATE TABLE "+database.getEdgeTable()+"; "+
            		     "INSERT INTO "+database.getEdgeTable()+" (seg_id, node1, node2) "+
            		     "VALUES ";
            
            StringBuilder sb = new StringBuilder();
            
            
            while((line = EdgeReader.readLine()) != null) {
            	sb.append(String.format("%s", MNTG_Edge(line)));
            }
            sql += sb.toString();
            sql = sql.substring(0, sql.length() - 1) + ";";

            database.updateQuery(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
                    
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
		String line;
		
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader NodeIn = new FileReader(NodeFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader NodeReader = new BufferedReader(NodeIn);
             
            System.out.print("Reading in nodes... ");
            
            long start_time = System.currentTimeMillis();
            
            String sql = "TRUNCATE TABLE "+database.getNodeTable()+"; "+
       		     "INSERT INTO "+database.getNodeTable()+"(node_id, lat, lon) VALUES ";
            
            StringBuilder sb = new StringBuilder();
            
            while((line = NodeReader.readLine()) != null){
            	sb.append(String.format("%s", MNTG_Node(line)));
            }
            sql += sb.toString();
            sql = sql.substring(0, sql.length() - 1) + ";";
            
            database.updateQuery(sql);
            long total_time = System.currentTimeMillis() - start_time;
            System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
                    
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
		
    	return "("+E_id+","+E_node1+","+E_node2+"),";
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
		
    	return "("+N_id+","+lat+","+lon+"),";
	}	
	
	public void partitionRN(){
		String sql = "SELECT MAX(lat), MIN(lat), MAX(lon), MIN(lon) "+
				     "FROM "+database.getNodeTable();
		
		List<Map<String, Object>> results = database.exicuteQuery(sql);
		
		double MaxLat = Double.parseDouble(results.get(0).get("MAX(lat)").toString());
		double MinLat = Double.parseDouble(results.get(0).get("MIN(lat)").toString());
		double MaxLon = Double.parseDouble(results.get(0).get("MAX(lon)").toString());
		double MinLon = Double.parseDouble(results.get(0).get("MIN(lon)").toString());
		
		int numberRows = ((int)database.distance(MaxLat, MaxLon, MinLat, MaxLon, "K") + 1);
		int numberColumns = ((int)database.distance(MaxLat, MaxLon, MaxLat, MinLon, "K") + 1);
		
		double latInc = Math.abs((MaxLat - MinLat)/numberRows); //keeps track of how much to increment the lat for each partition
		double lonInc = Math.abs((MaxLon - MinLon)/numberColumns); //keeps track of how much to increment the lon for each partition
		
		//-----------------------Begin Partition-------------------------------------
		double currentLat = MaxLat;
		double currentLon = MaxLon;
		
		sql = "TRUNCATE TABLE "+database.getRNIndexTable()+"; ";
		StringBuilder sb = new StringBuilder();
		
		System.out.print("Partitioning RN... ");
		long start_time = System.currentTimeMillis();
		
		String arg1, arg2;
		int j = 1, k = 1;
		while(currentLon >= MinLon){
			while(currentLat >= MinLat){
				arg1 = "INSERT INTO "+database.getRNIndexTable()+" VALUES("+"'"+j+"x"+k+"', "+currentLat+", "+currentLon+", "+(currentLat - latInc)+", "+(currentLon - lonInc)+"); ";
				arg2 = "CREATE TABLE "+j+"x"+k+" "+database.fileToString("files/PartitionRN.sql");
				
				sb.append(String.format("%s%s", arg1, arg2));
				currentLat -= latInc;
				k++;
			}
			currentLon -= lonInc; //decrease on row
			currentLat = MaxLat; //set to first column
			k = 1;
			j++;
		}
		sql += sb.toString();
		database.updateQuery(sql);
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
          
		
		return;
	}
	
	public void populatePartitionRN(){
		String sql = "TRUNCATE TABLE mergedNE; "+
					 "INSERT INTO mergedNE (seg_id, node1, node2, lat1, lon1, lat2, lon2) "+database.fileToString("files/MergeNE.sql");
		database.updateQuery(sql);
		
		List<Map<String, Object>> resultsIndexs = database.exicuteQuery("SELECT * FROM "+database.getRNIndexTable());
		
		
		double maxLat, maxLon, minLat, minLon;
		String tableInsert;
		String arg1;
		StringBuilder sb = new StringBuilder();
		
		System.out.print("Populating RNPs... ");
		long start_time = System.currentTimeMillis();
		
		for(int j = 0; j < resultsIndexs.size(); j++){
			tableInsert = resultsIndexs.get(j).get("table_id").toString();
			
			maxLat = Double.parseDouble(resultsIndexs.get(j).get("max_lat").toString());
			maxLon = Double.parseDouble(resultsIndexs.get(j).get("max_lon").toString());
			minLat = Double.parseDouble(resultsIndexs.get(j).get("min_lat").toString());
			minLon = Double.parseDouble(resultsIndexs.get(j).get("min_lon").toString());
			
			arg1 = "TRUNCATE TABLE "+tableInsert+"; "+
				   "INSERT INTO "+tableInsert+" (seg_id, node1, node2, lat1, lon1, lat2, lon2) "+
				   "SELECT seg_id, node1, node2, lat1, lon1, lat2, lon2 "+
				   "FROM mergedNE "+
				   "WHERE (lat1<="+maxLat+" AND lon1<="+maxLon+" AND lat1>="+minLat+" AND lon1>="+minLon+") OR ("+
				   "lat2<="+maxLat+" AND lon2<="+maxLon+" AND lat2>="+minLat+" AND lon2>="+minLon+"); ";
			
			sb.append(String.format("%s", arg1));
		}
		sql = sb.toString();
		database.updateQuery(sql);
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
	
	public int countPartitionEntries(){
		List<Map<String, Object>> resultsIndexs = database.exicuteQuery("SELECT * FROM "+database.getRNIndexTable());
		List<Map<String, Object>> partition;
		
		int entries = 0;
		String index;
		for(int j = 0; j < resultsIndexs.size(); j++){
				index = resultsIndexs.get(j).get("table_id").toString();
				partition = database.exicuteQuery("SELECT * FROM "+index);
				entries += partition.size();
		}
		return entries;
	}
}

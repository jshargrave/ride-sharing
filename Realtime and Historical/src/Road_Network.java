import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class Road_Network {	
	static String NodeFile = "files/Nodes.txt";
	static String EdgeFile = "files/Edges.txt";
	int TimeInc = 60; //time increment to partition road networks by in minutes, the increment should be a multiple of 24*60
	
	int partitionSize = 1; //measured in kilometers
	
	DBMS database = new DBMS(); //used to read in road network to database
	
	//reads in the edges
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
	
	//reads in the nodes
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
	
	//uses the MNTG method to parse a edge to sql to insert into the database
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
	
	//uses the MNTG method to parse a node to sql to insert into the database
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
	
	//partitions the road network and creates a index table and partitioned road network tables
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
				arg1 = "INSERT INTO "+database.getRNIndexTable()+" VALUES("+"'I"+j+"x"+k+"', "+currentLat+", "+currentLon+", "+(currentLat - latInc)+", "+(currentLon - lonInc)+"); ";
				arg2 = "CREATE TABLE I"+j+"x"+k+" "+database.fileToString("files/PartitionRN.sql");
				
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
	
	//populates the partitioned road network tables with segments
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
	
	//removes empty partitions
	public void purgePartitions(){
		System.out.print("Purging RNPs... ");
		long start_time = System.currentTimeMillis();
		
		List<Map<String, Object>> resultsIndexs = database.exicuteQuery("SELECT table_id FROM "+database.getRNIndexTable());
		List<Map<String, Object>> index;
		
		StringBuilder sb = new StringBuilder();
		String tableName, arg1;
		for(int i = 0; i < resultsIndexs.size(); i++){
			tableName = resultsIndexs.get(i).get("table_id").toString();
			index = database.exicuteQuery("SELECT seg_id FROM "+tableName);
			
			if(index.size() == 0){
				arg1 = "DROP TABLE "+tableName+"; DELETE FROM rn_index WHERE table_id='"+tableName+"';";
				sb.append(String.format("%s", arg1));
			}
		}
		database.updateQuery(sb.toString());
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
	
	/*
	public void mergePartitions(){
		int MinSegments = 100; //this is the minimuim amount of segments a partition can have without being considered for merging
		
		List<Map<String, Object>> resultsIndexs = database.exicuteQuery("SELECT * FROM "+database.getRNIndexTable());
		
		for(int i = 0; i < resultsIndexs.size(), i++){
			int size; //holds the size of the partition under consideration
			if(size < MinSegments){
				char Dir = MinDir(N, S, E, W); //finds the min segments in a North, South East, and West directions
				MergeForDir(MinDir); //takes the direction of min segments and merges the partitions
			}
		}
	}*/
	
	//partitions the road network into time partitions, based on the TimeInc variable
	public void timePartitions(){
		System.out.print("Partitioning time... ");
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
				
				arg1 = "CREATE TABLE "+tableNameTime+" (seg_id BIGINT PRIMARY KEY,speed FLOAT DEFAULT '-1');";
				sb1.append(String.format("%s", arg1));
				
				time = time.plusMinutes(TimeInc);
			}
		}
		database.updateQuery(sb1.toString());
		
		long total_time = System.currentTimeMillis() - start_time;
        System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
}

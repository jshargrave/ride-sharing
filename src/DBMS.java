import java.io.*;
import java.sql.*;
import java.util.*;


public class DBMS {
	//  Database credentials
	static String USER = "root";
	static String PASS = "";
	
	static String databaseName = "RIDESHARING";
	static String edgeTable = "tmp_edges";
	static String nodeTable = "tmp_nodes";
	static String GPSTable = "tmp_gps";
	static String incTable = "tmp_incidents";
	static String rnIndexTable = "rn_index";
	
	static String tableFile = "files/Tables.sql";
	
	// JDBC driver name and database URL
	static String DB_URL = "jdbc:mysql://localhost:3306/"+databaseName+"?allowMultiQueries=true";
	
	public void updateQuery(String sql) {
	   Connection conn = null;
	   Statement stmt = null;
	   try{
	      Class.forName("com.mysql.jdbc.Driver");
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      stmt = conn.createStatement();
		      
	      stmt.executeUpdate(sql);
	   }
	   catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }
	   catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }
	   finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            conn.close();
	      }
	      catch(SQLException se){
	      }// do nothing
	      try{
	         if(conn!=null)
	            conn.close();
	      }
	      catch(SQLException se){
	    	  se.printStackTrace();
	      }//end finally try
	   }//end try
	   //System.out.println("Goodbye!");
	   return;
	}
	
	/* Returns a List<Map<String, Object>>, list items can be accessed by List.get(int index), Map items can be accessed by Map.get("column name")*/
	public List<Map<String, Object>> exicuteQuery(String sql){
		Connection conn = null;
		Statement stmt = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    conn = DriverManager.getConnection(DB_URL, USER, PASS);
		    stmt = conn.createStatement();

		    ResultSet rs = stmt.executeQuery(sql);
		    ResultSetMetaData metaData = rs.getMetaData();
		    int columnCount = metaData.getColumnCount();
		    
		    
		    Map<String, Object> row = null;
		    while(rs.next()){
		    	row = new HashMap<String, Object>();
		    	for(int i = 1; i <= columnCount; i++){
		    		row.put(metaData.getColumnName(i), rs.getObject(i));
		    	}
		    	resultList.add(row);
		    }
		    
		
		    rs.close();
		}
		catch(SQLException se){
			//Handle errors for JDBC
		    se.printStackTrace();
		}
		catch(Exception e){
			//Handle errors for Class.forName
		    e.printStackTrace();
		}
		finally{
		    //finally block used to close resources
		    try{
		    	if(stmt!=null)
		            conn.close();
		    }
		    catch(SQLException se){
		    }// do nothing
		    try{
		    	if(conn!=null)
		            conn.close();
		    }
		    catch(SQLException se){
		    	se.printStackTrace();
		    }//end finally try
		}//end try
		return resultList;
	}
	
	private void buildTables(){
		String sql = fileToString(tableFile);
		updateQuery(sql);
		return;
	}
	public void rebuildDatabase(){
		System.out.printf("Clearing database...");
		long start_time = System.currentTimeMillis();
		String sql = "DROP DATABASE "+databaseName+"; " + 
				     "CREATE DATABASE "+databaseName+";";
		
		updateQuery(sql);
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		
		System.out.printf("Building tables...");
		start_time = System.currentTimeMillis();
		buildTables();
		total_time = System.currentTimeMillis() - start_time;
		System.out.println("\t\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		return;
	}
	
	public String fileToString(String filename){
		String line, sql = "";
		
		try{
			
			// FileReader reads text files in the default encoding.
            FileReader in = new FileReader(filename);

            // Always wrap FileReader in BufferedReader.
            BufferedReader reader = new BufferedReader(in);
            
            while((line = reader.readLine()) != null){
            	sql += line;
            }
                    
            reader.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file " + filename + " ...");
		}
		catch(IOException ex){
			System.out.println("Error reading file " + filename + " ...");
		}
		return sql;
	}
	
	public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
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
		else if(unit == "meters"){
			dist = dist * 1609.34;
		}

		return dist;
	}
	
	public boolean coordsInBox(double maxLat, double maxLon, double minLat, double minLon, double lat, double lon){
		if((lat <= maxLat && lat >= minLat) && (lon <= maxLon && lon >= minLon)){
			return true;
		}
		else{
			return false;
		}
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	public String getEdgeTable(){
		return edgeTable;
	}
	
	public String getNodeTable(){
		return nodeTable;
	}
	
	public String getIncTable(){
		return incTable;
	}
	
	public String getRNIndexTable(){
		return rnIndexTable;
	}
	
	public String getGPSTable(){
		return GPSTable;
	}
}

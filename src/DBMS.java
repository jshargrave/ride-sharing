import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBMS {
	//  Database credentials
	String USER = "root";
	String PASS = "";
	
	String databaseName = "RIDESHARING";
	String segTable = "segment";
	String nodeTable = "node";
	String incTable = "incident";
	
	// JDBC driver name and database URL
	String DB_URL = "jdbc:mysql://localhost:3306/"+databaseName+"?allowMultiQueries=true";
	
	public void query(String sql) {
	   Connection conn = null;
	   Statement stmt = null;
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");

	      //STEP 3: Open a connection
	      //System.out.println("Connecting to a selected database...");
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      //System.out.println("Connected database successfully...");
		      
	      //STEP 4: Execute a query
	      //System.out.println("running query...");
	      stmt = conn.createStatement();
		      
	      stmt.executeUpdate(sql);
	
	      //System.out.println("finished query...");

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
	
	public void getSegment(String EdgeID){
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
		    Class.forName("com.mysql.jdbc.Driver");

		    //STEP 3: Open a connection
		    //System.out.println("Connecting to a selected database...");
		    conn = DriverManager.getConnection(DB_URL, USER, PASS);
		    //System.out.println("Connected database successfully...");
		      
		    //STEP 4: Execute a query
		    //System.out.println("Creating statement...");
		    stmt = conn.createStatement();

		    String sql = "SELECT id, node1, node2 FROM "+segTable+" WHERE id="+EdgeID;
		    ResultSet rs = stmt.executeQuery(sql);
		    //STEP 5: Extract data from result set
		    while(rs.next()){
		    //Retrieve by column name
		    Double id  = rs.getDouble("id");
		    Double node1 = rs.getDouble("node1");
		    Double node2 = rs.getDouble("node2");
		         
		    System.out.println(id+", \t"+node1+", \t"+node2);
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
		return;
	}
	
	public void buildTables(){
		String sql = "CREATE TABLE " + segTable + " "+
				"(id BIGINT PRIMARY KEY, " +
				"node1 varchar(25), " +
				"node2 varchar(25)); " +
               
				"CREATE TABLE " + nodeTable + " "+
                "(id BIGINT PRIMARY KEY, " +
                "lat DOUBLE, " + 
                "lon DOUBLE); ";
		query(sql);
		return;
	}
	public void clear(){
		System.out.printf("Clearing database...");
		String sql = "DROP DATABASE "+databaseName+"; " + 
				     "CREATE DATABASE "+databaseName+";";
		
		query(sql);
		System.out.println("\tFinished clearing database");
		
		System.out.printf("Building tables...");
		buildTables();
		System.out.println("\tFinished building tables");
		return;
	}
	
	public String fileToString(String fileName){
		
		
		return "";
	}
}

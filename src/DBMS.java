import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBMS {
	// JDBC driver name and database URL
	String DB_URL = "jdbc:mysql://localhost:3306/ridesharing?allowMultiQueries=true";

	//  Database credentials
	String USER = "root";
	String PASS = "";
	
	public void query(String sql) {
	   Connection conn = null;
	   Statement stmt = null;
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");

	      //STEP 3: Open a connection
	      System.out.println("Connecting to a selected database...");
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      System.out.println("Connected database successfully...");
		      
	      //STEP 4: Execute a query
	      System.out.println("running query...");
	      stmt = conn.createStatement();
		      
	      stmt.executeUpdate(sql);
	
	      System.out.println("finished query...");

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
	   System.out.println("Goodbye!");
	   return;
	}
	
	public void buildTables(){
		String sql = "CREATE TABLE incident " +
				"(id INTEGER not NULL, " +
                " PRIMARY KEY ( id ))"; 
		query(sql);
		return;
	}
	public void deleteAll(){
		String sql = "DROP DATABASE RIDESHARING;" + 
				     "CREATE DATABASE RIDESHARING";
		query(sql);
		return;
	}
}

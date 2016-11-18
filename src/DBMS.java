import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBMS {
	//  Database credentials
	static String USER = "root";
	static String PASS = "";
	
	static String databaseName = "RIDESHARING";
	static String edgeTable = "tmp_edges";
	static String nodeTable = "tmp_nodes";
	static String incTable = "tmp_incidents";
	
	static String tableFile = "files/Tables.sql";
	
	// JDBC driver name and database URL
	static String DB_URL = "jdbc:mysql://localhost:3306/"+databaseName+"?allowMultiQueries=true";
	
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
	
	public ResultSet exicuteQuery(String sql){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
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

		    rs = stmt.executeQuery(sql);
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
		return rs;
	}
	
	private void buildTables(){
		String sql = fileToString(tableFile);
		query(sql);
		return;
	}
	public void rebuildDatabase(){
		System.out.printf("Clearing database...");
		long start_time = System.currentTimeMillis();
		String sql = "DROP DATABASE "+databaseName+"; " + 
				     "CREATE DATABASE "+databaseName+";";
		
		query(sql);
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		
		System.out.printf("Building tables...");
		start_time = System.currentTimeMillis();
		buildTables();
		total_time = System.currentTimeMillis() - start_time;
		System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
		return;
	}
	
	private String fileToString(String filename){
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
	
	public String getEdgeTable(){
		return edgeTable;
	}
	
	public String getNodeTable(){
		return nodeTable;
	}
	
	public String getIncTable(){
		return incTable;
	}
}

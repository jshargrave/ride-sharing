import java.lang.ClassNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBMS {
	public void connect(){
		Connection conn = null;
		Statement stmt = null;
		
		String url = "jdbc:mysql://localhost:3306/";
		String db = "Ride-Sharing";
		String user = "root";
		String pass = "";
		
	    try{
	        System.out.println("conneting to Database...");
	        Class.forName("com.mysql.jdbc.Driver");
	        conn = DriverManager.getConnection(url + db, user, pass);
	        System.out.println("Connection Successful");
	        stmt = conn.createStatement();
	    }
	    catch(ClassNotFoundException error){
	        System.out.println("Error:" + error.getMessage()); 
	    }
	
	    catch(SQLException error){
	        System.out.println("Error:" + error.getMessage());
	    }
	    finally{
	        if (conn != null) 
	            try {
	            	conn.close();
	            }
	        catch(SQLException ignore){
	
	        }
	
	        if (stmt != null) 
	            try {
	            	stmt.close();
	            }
	
	        catch(SQLException ignore){
	
	        }
	    }
	}
	
	public void insert() {
		// JDBC driver name and database URL
		   String DB_URL = "jdbc:mysql://localhost:3306/Ride-Sharing";

		   //  Database credentials
		   String USER = "root";
		   String PASS = "";

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
		      System.out.println("Inserting records into the table...");
		      stmt = conn.createStatement();
		      
		      String sql = "INSERT INTO segments " +
		                   "VALUES (101, '5', '7')";
		      stmt.executeUpdate(sql);
	
		      System.out.println("Inserted records into the table...");

		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   System.out.println("Goodbye!");
		   
	}

	public void delete(){
		
	}
	
	public void select(){
		
	}
	
	public void update(){
		
	}
	
	public void clear(){
		
	}
}

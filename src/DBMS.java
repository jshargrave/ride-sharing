import java.lang.ClassNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBMS {
	public Connection startConnection(){
	    Connection conn = null;
		Statement statement = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = "Ride-Sharing";
		String user = "root";
		String pass = "";
		
	    try{
	        System.out.println("conneting to Database...");
	        Class.forName("com.mysql.jdbc.Driver");
	        conn = DriverManager.getConnection(url + db, user, pass);
	        System.out.println("Connection Successful");
	
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
	
	        if (statement != null) 
	            try {
	                statement.close();
	            }
	
	        catch(SQLException ignore){
	
	        }
	    }
	    return conn;
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

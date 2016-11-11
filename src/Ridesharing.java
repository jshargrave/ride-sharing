public class Ridesharing {
	public static void main(String args[]){
		DBMS database = new DBMS();
		//database.clear();
		
		//Road_Network R = new Road_Network();
		//R.ReadInEdges();
		//R.ReadInNodes();
		
		long start_time = System.currentTimeMillis();
  
        
                
		database.getSegment("58087747");
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("\tCompleted: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	
	}
}


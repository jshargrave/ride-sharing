public class Ridesharing {
	public static void main(String args[]){
		//reset database
		DBMS database = new DBMS();
		database.clear();
		
		//read in rode network
		Road_Network R = new Road_Network();
		R.ReadInEdges();
		R.ReadInNodes();

		//read in incidents
		Incident I = new Incident();
		I.readInIncidents();
		
	}
}


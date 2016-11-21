public class Ridesharing {
	public static void main(String args[]){
		//-----------------------reset database------------------------
		//DBMS database = new DBMS();
		//database.rebuildDatabase();
		
		//-----------------------read in rode network------------------
		Road_Network R = new Road_Network();
		//R.ReadInEdges();
		//R.ReadInNodes();

		R.partitionRN();
		//-----------------------read in incidents---------------------
		//Incident I = new Incident();
		//I.readInIncidents();
	}
}


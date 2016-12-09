public class Ridesharing {
	public static void main(String args[]){
		//-----------------------reset database------------------------
		DBMS database = new DBMS();
		database.rebuildDatabase();
		
		//-----------------------read in rode network------------------
		Road_Network R = new Road_Network();
		R.ReadInEdges();
		R.ReadInNodes();
		R.partitionRN();
		R.populatePartitionRN();
		
		//-----------------------read in GPS data----------------------
		GPS_Data G = new GPS_Data();
		G.readInGPS();
		G.estimateSpeed();
		G.matchLogToSeg();
		
		//-----------------------read in incidents---------------------
		//Incident I = new Incident();
		//I.readInIncidents();
	}
}


public class Ridesharing {
	public static void main(String args[]){
        long start_time = System.currentTimeMillis();
        
		//-----------------------reset database------------------------
		DBMS database = new DBMS();
		database.rebuildDatabase();
		
		//-----------------------read in rode network------------------
		Road_Network R = new Road_Network();
		R.ReadInEdges();
		R.ReadInNodes();
		R.partitionRN();
		R.populatePartitionRN();
		R.purgePartitions();
		R.timePartitions();
		
		//-----------------------read in GPS data----------------------
		GPS_Data G = new GPS_Data();
		G.readInGPS();
		G.estimateSpeed();
		G.matchLogToSeg();
		G.AvgSpeedToSeg();
		
		//-----------------------read in incidents---------------------
		Incident I = new Incident();
		I.readInIncidents();
		
		long total_time = System.currentTimeMillis() - start_time;
		System.out.println("Total Time: " + total_time + " MilliSeconds, " + total_time/1000 + " Seconds, " + total_time/(1000 * 60) + " Mins");
	}
}


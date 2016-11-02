public class Ridesharing {
	public static void main(String args[]){
		Road_Network R = new Road_Network();
		GPS_Data G = new GPS_Data();
		//R.printSegments();
		
		//System.out.println("Finished");
		R.SearchSegments();
	}
}

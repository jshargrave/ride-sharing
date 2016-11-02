
public class Segment {
	String id;
	String node1;
	String node2;
	
	double lat1, lat2, lon1, lon2;
	
	public Segment(String Eid, String Enode1, String Enode2, double Elat1, double Elat2, double Elon1, double Elon2){
		id = Eid;
		node1 = Enode1;
		node2 = Enode2;
		lat1 = Elat1;
		lat2 = Elat2;
		lon1 = Elon1;
		lon2 = Elon2;
	}
}


#include <iostream>
#include "Search.h"
#include "RoadNetwork.h"
using namespace std;

int main()
{
    string out = "files/output1.txt";

    RoadNetwork R1; //used to store the road network and functionality

    Search<map<string, RN_node*> > S1;
    cout<<S1.check_for_update(R1.RN)<<endl;
    S1.a_star("26353013", "237624380", R1.RN, out);

    cout<<S1.check_for_update(R1.RN)<<endl;
    return 0;
}

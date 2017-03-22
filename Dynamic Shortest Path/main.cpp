#include <iostream>
#include "Search.h"
#include "RoadNetwork.h"
using namespace std;

unsigned long random_number(unsigned long x);
void update_expanded_tree(RoadNetwork &R, Search<map<string, RN_node*>* > &S);

int main()
{
    string out = "files/output1.txt";

    RoadNetwork R1; //used to store the road network and functionality

    Search<map<string, RN_node*>* > S1;
    cout<<S1.check_for_update(R1.RN())<<endl;
    S1.a_star("26353013", "237624380", R1.RN(), out);


    //update Road Network
    for(int i = 0; i < 10; i++) {
        update_expanded_tree(R1, S1);
        cout<<S1.check_for_update(R1.RN())<<endl;
        S1.a_star("26353013", "237624380", R1.RN(), out);
    }

    cout<<"Program finished successfully"<<endl;
    return 0;
}

unsigned long random_number(unsigned long x)
{
    srand(static_cast<unsigned int>(time(NULL)));
    return (rand() % x);
}

void update_expanded_tree(RoadNetwork &R, Search<map<string, RN_node*>* > &S)
{
    for(map<string, float>::iterator it = S.expanded_tree_ptr()->begin(); it != S.expanded_tree_ptr()->end(); it++){
        R.RN()->at(it->first)->speed = R.random_speed();
    }
}
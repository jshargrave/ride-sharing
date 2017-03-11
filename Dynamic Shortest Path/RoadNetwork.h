#include <map>
#include <vector>
#include <string>
#include <fstream>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <map>
#include <algorithm>

using namespace std;

#define pi 3.14159265358979323846
#define earthRadiusKm 6371.0

#ifndef ROADNETWORK_H
#define ROADNETWORK_H



struct RN_node
{
    public:

    float speed;
    vector<double> coords;
    vector<string> edges;

    RN_node(float s, double lat, double lon)
    {
        speed = s;
        coords.push_back(lat);
        coords.push_back(lon);
    }
};

class RoadNetwork
{
    public:

    map<string, RN_node*> RN;

    RoadNetwork()
    {
        load_nodes();
        load_edges();
    }

    ~RoadNetwork()
    {
        for(map<string, RN_node*>::iterator it = RN.begin(); it != RN.end(); it++)
        {
            delete it -> second;
        }
    }

    void load_nodes()
    {
        string line;
        fstream file("files/Nodes.txt");
        if(!file.is_open())
        {
            cout<<"Error: could not open "<<"files/Nodes.txt"<<endl;
        }

        unsigned long start, end;
        string id;
        double lat, lon;
        RN_node* node_ptr;

        int i = 0;
        while(getline(file, line))
        {
            start = 0;
            end = line.find(',');
            id = line.substr(start, end - start);

            start = end + 1;
            end = line.find(',', start);
            lat = strtod(line.substr(start, end - start).c_str(), NULL);

            start = end + 1;
            end = line.length();
            lon = strtod(line.substr(start, end - start).c_str(), NULL);

            if(id != "") {
                node_ptr = new RN_node(random_speed(), lat, lon);
                RN[id] = node_ptr;
                i++;
            } else cout<<"Error: tried to insert blank node"<<endl;
        }
        cout<<"Loaded "<<i<<" nodes"<<endl;

        file.close();
    }

    void load_edges()
    {
        string line;
        fstream file("files/Edges.txt");
        if(!file.is_open())
        {
            cout<<"Error: could not open "<<"files/Edges.txt"<<endl;
        }

        unsigned long start, end;
        string id, edge;

        int i = 0;
        while(getline(file, line))
        {
            start = line.find(',') + 1;
            end = line.find(',', start);
            id = line.substr(start, end - start);

            start = end + 1;
            end = line.find(',', start);
            edge = line.substr(start, end - start);

            if(id != "")
            {
                RN[id] -> edges.push_back(edge);
                i++;
            } else cout<<"Error: tried to insert blank edge"<<endl;
        }
        cout<<"Loaded "<<i<<" edges"<<endl;

        file.close();
    }

    float random_speed()
    {
        srand(static_cast<unsigned int>(time(NULL)));
        return (rand() % 56) + 10;
    }
};


#endif // ROADNETWORK_H

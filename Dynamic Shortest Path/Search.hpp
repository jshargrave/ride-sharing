//
// Created by Joseph on 2/23/2017.
//

using namespace std;

template <typename T>
Search<T>::Search()
{
	_expanded_tree_ptr = NULL;
	upper_bound = 0;
}

template <typename T>
Search<T>::~Search()
{
    delete _expanded_tree_ptr;
}

template <typename T>
double Search<T>::get_cost(string s, string d, string p, T& RN)
{
    return get_dist(s, d, RN) + get_time(s, p, RN);
}

template <typename T>
void Search<T>::a_star(string s, string d, T& RN, string out, clock_t time /*=clock()*/)
{
    //stores the frontier and explored maps
    frontier_type frontier;

    map<string, string>* frontier_map_ptr = new map<string, string>;
    map<string, float>* explored_map_ptr = new map<string, float>;

    //used to hold the edges from a node
    vector<string>* edges_ptr;

    //initial node
    //node.set_values(id, prev, cost, speed, dist)
    Search_node node(s, "-1", get_cost(s, d, "-1", RN), RN -> at(s) -> speed, 0);

    //loading start node
    frontier.push(node);
    frontier_map_ptr -> insert(pair<string, string>(s, "-1"));

    //variables for running through the loop
    double cost, dist;
    float speed;
    string e;

    while (!frontier.empty())
    {
        //getting node from frontier
        node = frontier.top();
        frontier.pop();

        //adding node to explored and removing from frontier_map
        explored_map_ptr -> insert(pair<string, float>(node.id, node.speed));
        frontier_map_ptr -> erase(node.id);

        //found goal node test
        if (node.id == d) {
            break;
        }

        edges_ptr = &RN->at(node.id) -> edges;
        for(vector<string>::iterator it = edges_ptr -> begin(); it != edges_ptr -> end(); it++)
        {
            e = *it;
            cost = get_cost(e, d, node.prev, RN);
            speed = RN->at(e) -> speed;
            dist = node.total_dist + get_dist(node.id, e, RN);

            if (explored_map_ptr -> find(e) == explored_map_ptr -> end() && frontier_map_ptr -> find(e) == frontier_map_ptr -> end())
            {
                //node.set_values(id, prev, cost, speed, dist)
                node.set_values(e, node.id, cost, speed, dist);
                frontier.push(node);
                frontier_map_ptr -> insert(pair<string, string>(e, node.id));
            }
        } //edges
    } //frontier

    cout << "A*: " << (clock() - time) / (double) CLOCKS_PER_SEC << endl;
    //output(node.id, explored_map_ptr, out, time);

	if (_expanded_tree_ptr != NULL)
	{
		delete _expanded_tree_ptr;
	}
    delete frontier_map_ptr;


    _expanded_tree_ptr = explored_map_ptr;
    explored_map_ptr = NULL;
    edges_ptr = NULL;
    return;

} //a_star

template <typename T>
bool Search<T>::check_for_update(T& RN_ptr)
{
    if(_expanded_tree_ptr != NULL)
    {
        for (map<string, float>::iterator it = _expanded_tree_ptr->begin(); it != _expanded_tree_ptr->end(); it++)
        {
            //cout<<RN[it->first]->speed<<", "<<it -> second<<endl;
            if (RN_ptr -> at(it->first)->speed != it->second)
            {
                return true;
            }
        }
    }
    return false;
}

template <typename T>
void Search<T>::output(string goal, map<string, string>* &explored_map_ptr, string out, clock_t time) {
    ofstream file;
    file.open(out.c_str());

    vector<string> path;
    string id = goal;

    while (id != "-1") {
        path.push_back(id);
        id = explored_map_ptr -> at(id);
    }
    reverse(path.begin(), path.end());

    for(vector<string>::iterator it = path.begin(); it != path.end(); it++)
    {
        file<<*it<<",";
    }
    return;
}

template <typename T>
double Search<T>::get_time(string s, string p, T& RN)
{
    if(p != "-1") {
        double lat1 = RN->at(s)->coords[0];
        double lon1 = RN->at(s)->coords[1];
        double lat2 = RN->at(p)->coords[0];
        double lon2 = RN->at(p)->coords[1];

        return RN->at(p)->speed / distanceEarth(lat1, lon1, lat2, lon2);
    } else return 0;
}

// Returns the straight line distance between two nodes s, and d.
template <typename T>
double Search<T>::get_dist(string s, string d, T& RN)
{
    double lat1 = RN->at(s) -> coords[0];
    double lon1 = RN->at(s) -> coords[1];
    double lat2 = RN->at(d) -> coords[0];
    double lon2 = RN->at(d) -> coords[1];

    return distanceEarth(lat1, lon1, lat2, lon2);
}

// This function converts decimal degrees to radians
template <typename T>
double Search<T>::deg2rad(double deg) {
    return (deg * pi / 180);
}

//  This function converts radians to decimal degrees
template <typename T>
double Search<T>::rad2deg(double rad) {
    return (rad * 180 / pi);
}

/**
 * Returns the distance between two points on the Earth.
 * Direct translation from http://en.wikipedia.org/wiki/Haversine_formula
 * @param lat1d Latitude of the first point in degrees
 * @param lon1d Longitude of the first point in degrees
 * @param lat2d Latitude of the second point in degrees
 * @param lon2d Longitude of the second point in degrees
 * @return The distance between the two points in kilometers
 */
 template <typename T>
double Search<T>::distanceEarth(double lat1d, double lon1d, double lat2d, double lon2d) {
    double lat1r, lon1r, lat2r, lon2r, u, v;
    lat1r = deg2rad(lat1d);
    lon1r = deg2rad(lon1d);
    lat2r = deg2rad(lat2d);
    lon2r = deg2rad(lon2d);
    u = sin((lat2r - lat1r)/2);
    v = sin((lon2r - lon1r)/2);
    return 2.0 * earthRadiusKm * asin(sqrt(u * u + cos(lat1r) * cos(lat2r) * v * v));
}
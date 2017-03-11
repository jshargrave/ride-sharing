//
// Created by Joseph on 2/23/2017.
//

using namespace std;

double Search::get_cost(string s, string d, string p)
{
    return get_dist(s, d) + get_time(s, p);
}

bool Search::check_for_update(map_type &explored)
{
    for(map_type::iterator it = explored.begin(); it != explored.end(); it++)
    {
        if(it -> second.cost == get_dist(it -> second.id, it -> second.prev))
            return true;
    }
    return false;
}

void Search::a_star(string s, string d, map_type &explored, string out, clock_t time /*=clock()*/)
{
    //stores the frontier in a priority queue sorted by the distance from the goal node
    frontier_type frontier;
    map<string, string> frontier_map;
    vector<string>* edges_ptr; //used to hold the edges from a node

    double upper_bound = numeric_limits<double>::max();

    Search_node node(s, "-1", get_cost(s, d, "-1"), R1.RN[s] -> speed, 0);

    if(explored.empty() || check_for_update(explored))
    {
        if(!explored.empty()) {
            upper_bound = explored.at(d).total_dist;
        }

        //loading start node
        frontier.push(node);
        explored.insert(explored.begin(), pair<string, Search_node>(node.id, node));

        double cost, dist;
        float speed;
        string e;

        while (!frontier.empty()) {
            //getting node from frontier
            node = frontier.top();
            frontier.pop();

            explored.insert(explored.begin(), pair<string, Search_node>(node.id, node));
            frontier_map.erase(node.id);

            //found goal node test
            if (node.id == d) {
                break;
            }

            edges_ptr = &R1.RN[node.id]->edges;
            while (!edges_ptr->empty()) {
                e = edges_ptr->back();
                edges_ptr->pop_back();
                cost = get_cost(e, d, node.prev);
                speed = node.speed;
                dist = node.total_dist + get_dist(node.id, e);

                if (explored.find(e) == explored.end() && frontier_map.find(e) == frontier_map.end() &&
                    dist <= upper_bound)
                {
					node.set_values(e, node.id, cost, speed, dist);
                    frontier.push(node);
                    frontier_map[e] = node.id;
                }
            } //edges
        } //frontier
    } //if condition

    cout << "A*: " << (clock() - time) / (double) CLOCKS_PER_SEC << endl;
    output(node.id, explored, out, time);
} //a_star

void Search::output(string goal, map_type &explored, string out, clock_t time) {
    ofstream file;
    file.open(out.c_str());

    vector<string> path;
    string id = goal;

    while (id != "-1") {
        path.push_back(id);
        id = explored.at(id).prev;
    }
    reverse(path.begin(), path.end());

    for(vector<string>::iterator it = path.begin(); it != path.end(); it++)
    {
        file<<*it<<",";
    }
    return;
}

double Search::get_time(string s, string p)
{
    if(p != "-1") {
        double lat1 = R1.RN[s]->coords[0];
        double lon1 = R1.RN[s]->coords[1];
        double lat2 = R1.RN[p]->coords[0];
        double lon2 = R1.RN[p]->coords[1];

        return R1.RN[p]->speed / distanceEarth(lat1, lon1, lat2, lon2);
    } else return 0;
}

// Returns the straight line distance between two nodes s, and d.
double Search::get_dist(string s, string d)
{
    double lat1 = R1.RN[s] -> coords[0];
    double lon1 = R1.RN[s] -> coords[1];
    double lat2 = R1.RN[d] -> coords[0];
    double lon2 = R1.RN[d] -> coords[1];

    return distanceEarth(lat1, lon1, lat2, lon2);
}

// This function converts decimal degrees to radians
double Search::deg2rad(double deg) {
    return (deg * pi / 180);
}

//  This function converts radians to decimal degrees
double Search::rad2deg(double rad) {
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
double Search::distanceEarth(double lat1d, double lon1d, double lat2d, double lon2d) {
    double lat1r, lon1r, lat2r, lon2r, u, v;
    lat1r = deg2rad(lat1d);
    lon1r = deg2rad(lon1d);
    lat2r = deg2rad(lat2d);
    lon2r = deg2rad(lon2d);
    u = sin((lat2r - lat1r)/2);
    v = sin((lon2r - lon1r)/2);
    return 2.0 * earthRadiusKm * asin(sqrt(u * u + cos(lat1r) * cos(lat2r) * v * v));
}
#include <queue>
#include <ctime>
#include <fstream>
#include <limits>
#include <iostream>
#include <map>
#include <math.h>
#include <algorithm>
using namespace std;

#ifndef SEARCH_H
#define SEARCH_H

double pi = 3.141592653589793238462643;
int earthRadiusKm = 6371;

struct Search_node
{
    string id;
    string prev;
    double cost;
    float speed;
    double total_dist;

    Search_node(string i, string p, double c, float s, double d)
    {
        id = i;
        prev = p;
        cost = c;
        total_dist = d;
    }

    void set_values(string i, string p, double c, float s, double d)
    {
        id = i;
        prev = p;
        cost = c;
        speed = s;
        total_dist = d;
    }
};

struct LessDistance
{
    bool operator()(const Search_node &rhs, const Search_node &lhs)const
    {
        return rhs.cost < lhs.cost;
    }
};

template <typename T>
class Search
{
    private:
        map<string, float>* expanded_tree_ptr;

        double upper_bound;

        typedef priority_queue<Search_node, vector<Search_node>, LessDistance> frontier_type;

    public:
	Search();
    ~Search();

    double get_cost(string s, string d, string p, T& RN);

    void a_star(string s, string d, T& RN, string out, clock_t time=clock());
    bool check_for_update(T& RN);
    void output(string goal, map<string, string>* &explored, string out, clock_t time);
    double get_time(string s, string p, T& RN);
    // Returns the straight line distance between two nodes s, and d.
    double get_dist(string s, string d, T& RN);
    // This function converts decimal degrees to radians
    double deg2rad(double deg);
    //  This function converts radians to decimal degrees
    double rad2deg(double rad);
    /**
     * Returns the distance between two points on the Earth.
     * Direct translation from http://en.wikipedia.org/wiki/Haversine_formula
     * @param lat1d Latitude of the first point in degrees
     * @param lon1d Longitude of the first point in degrees
     * @param lat2d Latitude of the second point in degrees
     * @param lon2d Longitude of the second point in degrees
     * @return The distance between the two points in kilometers
     */
    double distanceEarth(double lat1d, double lon1d, double lat2d, double lon2d);
};
#include "Search.hpp"
#endif // SEARCH_H

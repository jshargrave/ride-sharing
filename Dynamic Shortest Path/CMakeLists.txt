cmake_minimum_required(VERSION 3.6)
project(Dynamic_Shortest_Path)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
        main.cpp
        RoadNetwork.h
        Search.h)

add_executable(Dynamic_Shortest_Path ${SOURCE_FILES})
CREATE TABLE tmp_edges(
	seg_id BIGINT PRIMARY KEY,
	node1 varchar(25),
	node2 varchar(25));
	
CREATE TABLE tmp_nodes (
	node_id BIGINT PRIMARY KEY,
	lat DOUBLE,
	lon DOUBLE);
	
CREATE TABLE segments (
	seg_id BIGINT PRIMARY KEY,
	lat1 DOUBLE,
	lon1 DOUBLE,
	lat2 DOUBLE,
	lon2 DOUBLE);
	    
CREATE TABLE tmp_incidents(
	inc_id VARCHAR(50) PRIMARY KEY,
	lat DOUBLE,
	lon DOUBLE,
	category INTEGER,
	from_road VARCHAR(100),
	to_road VARCHAR(100),
	distance_delay INTEGER,
	delay_time INTEGER);
	
CREATE TABLE rn_index(
	table_id VARCHAR(50) PRIMARY KEY,
	max_lat DOUBLE,
	max_lon DOUBLE,
	min_lat DOUBLE,
	min_lon DOUBLE);
	
CREATE TABLE taxi_index (
	taxi_id INTEGER PRIMARY KEY,
	table_id INTEGER,
	FOREIGN KEY (table_id) REFERENCES RN_index(table_id));
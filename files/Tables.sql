CREATE TABLE segment(
	id BIGINT PRIMARY KEY,
	node1 varchar(25),
	node2 varchar(25));
	   
CREATE TABLE node(
	id BIGINT PRIMARY KEY,
	lat DOUBLE, 
	lon DOUBLE);
	    
CREATE TABLE incident(
	id VARCHAR(50) PRIMARY KEY,
	lat DOUBLE,
	lon DOUBLE,
	category INTEGER,
	from_road VARCHAR(100),
	to_road VARCHAR(100),
	distance_delay INTEGER,
	delay_time INTEGER);